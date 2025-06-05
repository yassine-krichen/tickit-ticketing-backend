# TickIT AI Module — Performance Measurements

## Task 1 · RAG Upgrade

**Method:** Replace flat-file prompt stuffing (keyword DB search → dump 5 tickets as text) with ChromaDB semantic retrieval (sentence-transformers `all-MiniLM-L6-v2`, top-3 chunks, FastAPI on port 8001).

**Data:** 90 synthetic FTTH blockage records, 9 categories, ingested via `ai-module/ingest.py`.

| Metric | Before (flat-file) | After (RAG top-3) | Delta |
|---|---|---|---|
| Avg prompt tokens | 612.1 | 244.1 | −368 tok |
| Token reduction % | — | — | **−60.1%** |
| Avg retrieval latency (ms) | n/a | 20.4 ms | — |
| Precision@1 (20 queries) | — | 18/20 = **0.90** | — |

> Full results: `measurements/rag_comparison.json`

---

## Task 2 · WebSocket Load Test

**Method:** k6 script — 30 concurrent STOMP clients, 50 ticket state change events, measure delivery latency (embed timestamp in payload, compute on MESSAGE receipt).

| Metric | Value |
|---|---|
| Concurrent clients | 30 |
| Events fired | 50 |
| P50 latency (ms) | — |
| P95 latency (ms) | — |
| P99 latency (ms) | — |
| Drop rate (%) | — |

> Full results: `measurements/socket_load_test.json`

---

## Task 3 · AI Pipeline Latency

**Method:** Spring `StopWatch` instrumentation in `AIServiceImpl.suggestResolution()` — 3 segments: `contextRetrieval`, `llmCall`, `responseParser`. 20 synthetic tickets benchmarked via `POST /api/v1/ai/benchmark`.

| Metric | ms |
|---|---|
| Min total | — |
| Max total | — |
| Avg total | — |
| P95 total | — |
| Avg contextRetrieval | — |
| Avg llmCall | — |
| Avg responseParser | — |

> Full results: `measurements/pipeline_latency.json`
