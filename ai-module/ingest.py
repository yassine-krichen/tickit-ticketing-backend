"""
Ingests historical blockage records into ChromaDB using sentence-transformers embeddings.
Run once before starting rag_service.py.

Usage:
    cd ai-module
    pip install -r requirements.txt
    python ingest.py
"""

import json
import os
import chromadb
from sentence_transformers import SentenceTransformer

DATA_PATH = os.path.join(os.path.dirname(__file__), "data", "blockage_records.json")
CHROMA_PATH = os.path.join(os.path.dirname(__file__), "chroma_db")
COLLECTION_NAME = "blockage_history"


def main():
    print("Loading blockage records...")
    with open(DATA_PATH, "r", encoding="utf-8") as f:
        records = json.load(f)

    print(f"Loaded {len(records)} records.")

    print("Loading embedding model (all-MiniLM-L6-v2)...")
    model = SentenceTransformer("all-MiniLM-L6-v2")

    print("Connecting to ChromaDB...")
    client = chromadb.PersistentClient(path=CHROMA_PATH)

    # Drop existing collection if re-running
    try:
        client.delete_collection(COLLECTION_NAME)
        print("Deleted existing collection.")
    except Exception:
        pass

    collection = client.create_collection(
        name=COLLECTION_NAME,
        metadata={"hnsw:space": "cosine"},
    )

    print("Generating embeddings and ingesting...")
    ids = []
    documents = []
    embeddings = []
    metadatas = []

    for rec in records:
        # Combine description + resolution for richer embedding
        doc_text = f"{rec['description']} Resolution: {rec['resolution_steps']}"
        ids.append(rec["id"])
        documents.append(doc_text)
        metadatas.append({
            "blockage_type": rec["blockage_type"],
            "duration_hours": rec["duration_hours"],
            "resolution_steps": rec["resolution_steps"],
            "outcome": rec["outcome"],
            "description": rec["description"],
        })

    embeddings = model.encode(documents, show_progress_bar=True).tolist()

    collection.add(
        ids=ids,
        documents=documents,
        embeddings=embeddings,
        metadatas=metadatas,
    )

    print(f"\nIngestion complete. {len(records)} records stored in ChromaDB at {CHROMA_PATH}")
    print("Run 'python rag_service.py' to start the retrieval API.")


if __name__ == "__main__":
    main()
