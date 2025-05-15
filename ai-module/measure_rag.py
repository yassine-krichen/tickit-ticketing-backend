"""
RAG comparison measurement script.
Measures:
  - Prompt token count before (flat-file dump of 5 tickets) vs after (top-3 RAG chunks)
  - Average retrieval latency for the vector search step
  - Precision@1 over 20 test queries (manual relevance ground truth)

Run after ingest.py:
    python measure_rag.py

Saves results to ../measurements/rag_comparison.json
"""

import json
import os
import time
import chromadb
import tiktoken
from sentence_transformers import SentenceTransformer

CHROMA_PATH = os.path.join(os.path.dirname(__file__), "chroma_db")
COLLECTION_NAME = "blockage_history"
DATA_PATH = os.path.join(os.path.dirname(__file__), "data", "blockage_records.json")
OUTPUT_PATH = os.path.join(os.path.dirname(__file__), "..", "measurements", "rag_comparison.json")

# Token encoder (approximation using cl100k_base which is similar to Llama tokenizers)
enc = tiktoken.get_encoding("cl100k_base")


def count_tokens(text: str) -> int:
    return len(enc.encode(text))


def build_before_context(records: list, limit: int = 5) -> str:
    """
    Simulates what Java AIPromptBuilder.buildResolutionPrompt outputs:
    5 DB tickets formatted with all relevant fields (subject, type, description,
    resolution steps, case history summary, duration). Matches real Spring output.
    """
    sections = []
    for i, r in enumerate(records[:limit], 1):
        blockage_label = r['blockage_type'].replace('_', ' ').title()
        sections.append(
            f"Case {i}:\n"
            f"  Subject: FTTH Installation Blockage - {blockage_label}\n"
            f"  Type: FTTH_INSTALLATION | Priority: MEDIUM\n"
            f"  Status: RESOLVED | Duration: {r['duration_hours']} hours\n"
            f"  Description: {r['description']}\n"
            f"  Resolution Applied: {r['resolution_steps']}\n"
            f"  Outcome: {r['outcome']}\n"
            f"  Case History: [Initial report] → [Field investigation] → [Resolution action] → [Closure verification]"
        )
    return "\n\n".join(sections)


def build_after_context(chunks: list) -> str:
    """Formats top-3 RAG chunks — same as AIPromptBuilder.buildRagResolutionPrompt context."""
    parts = []
    for i, chunk in enumerate(chunks, 1):
        parts.append(
            f"Record {i}:\n"
            f"  Blockage Type: {chunk['blockage_type']}\n"
            f"  Description: {chunk['description']}\n"
            f"  Resolution: {chunk['resolution_steps']}\n"
            f"  Duration: {chunk['duration_hours']} hours | Outcome: {chunk['outcome']}"
        )
    return "\n\n".join(parts)


# 20 test queries with expected top-1 blockage_type (ground truth)
TEST_QUERIES = [
    ("Cable cut during road excavation by water company", "cable_damage"),
    ("Fiber broken by crane at construction site", "cable_damage"),
    ("ONT device not responding after power surge", "equipment_failure"),
    ("Permit for road trench work rejected by municipality", "permit_delay"),
    ("Client not home for scheduled fiber installation", "access_issues"),
    ("Missing fiche navette document for treatment act", "documentation_issues"),
    ("GIS data shows wrong duct route in field survey", "si_terrain_mismatch"),
    ("Heavy rain preventing outdoor fiber splicing", "weather_conditions"),
    ("No FTTH technician available for deployment", "resource_shortage"),
    ("Optical signal attenuation too high on new cable", "signal_quality"),
    ("Vandalized fiber cable at street distribution box", "cable_damage"),
    ("Environmental objection blocking municipal permit", "permit_delay"),
    ("Security guard denying access to equipment room", "access_issues"),
    ("OLT line card failure at central office", "equipment_failure"),
    ("NMS shows wrong subscriber count for cabinet", "si_terrain_mismatch"),
    ("Cable documentation incomplete after sub-contractor work", "documentation_issues"),
    ("Fiber route flooded during heavy storm", "weather_conditions"),
    ("Equipment stock exhausted at regional depot", "resource_shortage"),
    ("Contaminated connectors in distribution cabinet", "signal_quality"),
    ("Fiber cable damaged by rodents near agricultural area", "cable_damage"),
]


def main():
    print("Loading data and models...")
    with open(DATA_PATH, "r", encoding="utf-8") as f:
        all_records = json.load(f)

    model = SentenceTransformer("all-MiniLM-L6-v2")
    client = chromadb.PersistentClient(path=CHROMA_PATH)
    collection = client.get_collection(COLLECTION_NAME)

    print(f"Collection has {collection.count()} records.")

    before_token_counts = []
    after_token_counts = []
    retrieval_latencies_ms = []
    precision_at_1_results = []

    print("\nRunning 20 test queries...\n")

    for i, (query, expected_type) in enumerate(TEST_QUERIES):
        print(f"Query {i+1:2d}: {query[:60]}...")

        # ---- BEFORE: flat-file context (5 random records) ----
        import random
        random.seed(i)
        sampled = random.sample(all_records, min(5, len(all_records)))
        before_ctx = build_before_context(sampled, limit=5)
        before_prompt = (
            "Suggest resolution for FTTH ticket:\n"
            f"Subject: {query}\n\n"
            f"Similar Cases:\n{before_ctx}\n\n"
            "Provide: 1. Primary Action 2. Required Steps 3. Known Risks"
        )
        before_tokens = count_tokens(before_prompt)
        before_token_counts.append(before_tokens)

        # ---- AFTER: RAG top-3 retrieval ----
        t0 = time.time()
        query_embedding = model.encode([query]).tolist()[0]
        results = collection.query(
            query_embeddings=[query_embedding],
            n_results=3,
            include=["metadatas", "distances"],
        )
        latency_ms = (time.time() - t0) * 1000
        retrieval_latencies_ms.append(latency_ms)

        chunks = []
        top1_type = None
        if results and results["metadatas"] and results["metadatas"][0]:
            for meta, dist in zip(results["metadatas"][0], results["distances"][0]):
                chunks.append({
                    "blockage_type": meta.get("blockage_type", "unknown"),
                    "description": meta.get("description", ""),
                    "resolution_steps": meta.get("resolution_steps", ""),
                    "duration_hours": meta.get("duration_hours", 0),
                    "outcome": meta.get("outcome", "unknown"),
                    "distance": round(dist, 4),
                })
            if chunks:
                top1_type = chunks[0]["blockage_type"]

        after_ctx = build_after_context(chunks)
        after_prompt = (
            "Suggest resolution for FTTH ticket:\n"
            f"Subject: {query}\n\n"
            f"Top-3 Historical Records:\n{after_ctx}\n\n"
            "Provide: 1. Primary Action 2. Required Steps 3. Known Risks"
        )
        after_tokens = count_tokens(after_prompt)
        after_token_counts.append(after_tokens)

        relevant = (top1_type == expected_type)
        precision_at_1_results.append({
            "query": query,
            "expected": expected_type,
            "top1_retrieved": top1_type,
            "relevant": relevant,
        })

        status = "PASS" if relevant else "FAIL"
        print(f"         [{status}] top1={top1_type} | before={before_tokens}tok | after={after_tokens}tok | latency={latency_ms:.1f}ms")

    # ---- Summary ----
    avg_before = sum(before_token_counts) / len(before_token_counts)
    avg_after = sum(after_token_counts) / len(after_token_counts)
    reduction_pct = (1 - avg_after / avg_before) * 100
    avg_latency = sum(retrieval_latencies_ms) / len(retrieval_latencies_ms)
    precision_at_1 = sum(1 for r in precision_at_1_results if r["relevant"]) / len(precision_at_1_results)

    print(f"\n=== RAG MEASUREMENT RESULTS ===")
    print(f"Avg prompt tokens BEFORE (flat-file): {avg_before:.1f}")
    print(f"Avg prompt tokens AFTER  (RAG top-3): {avg_after:.1f}")
    print(f"Token reduction:                       {reduction_pct:.1f}%")
    print(f"Avg RAG retrieval latency:             {avg_latency:.1f}ms")
    print(f"Precision@1 (20 queries):              {sum(1 for r in precision_at_1_results if r['relevant'])}/20 = {precision_at_1:.2f}")

    output = {
        "summary": {
            "avg_tokens_before": round(avg_before, 1),
            "avg_tokens_after": round(avg_after, 1),
            "token_reduction_pct": round(reduction_pct, 1),
            "avg_retrieval_latency_ms": round(avg_latency, 2),
            "precision_at_1": round(precision_at_1, 3),
            "precision_at_1_raw": f"{sum(1 for r in precision_at_1_results if r['relevant'])}/20",
            "total_records_in_store": collection.count(),
        },
        "per_query": [
            {
                "index": i + 1,
                "query": r["query"],
                "expected_type": r["expected"],
                "top1_type": r["top1_retrieved"],
                "relevant": r["relevant"],
                "before_tokens": before_token_counts[i],
                "after_tokens": after_token_counts[i],
                "retrieval_latency_ms": round(retrieval_latencies_ms[i], 2),
            }
            for i, r in enumerate(precision_at_1_results)
        ],
    }

    os.makedirs(os.path.dirname(OUTPUT_PATH), exist_ok=True)
    with open(OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"\nSaved to {OUTPUT_PATH}")


if __name__ == "__main__":
    main()
