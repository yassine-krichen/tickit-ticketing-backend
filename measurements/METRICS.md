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

**Method:** k6 script — 30 concurrent STOMP clients over native WebSocket (`/ws`), VU 1 fires 50 ticket state events to `/app/echo`, measures roundtrip latency via embedded `sentAt` timestamp.

| Metric | Value |
|---|---|
| Concurrent clients | 30 |
| Events measured | 100 (2 iterations × 50) |
| Min latency (ms) | 2 |
| Avg latency (ms) | 8 |
| P90 latency (ms) | 7 |
| P95 latency (ms) | 35 |
| Max latency (ms) | 86 |
| Connection errors | 0 |
| Drop rate (%) | 0 |

> Full results: `measurements/socket_load_test.json`

---

## Task 3 · AI Pipeline Latency

**Method:** Spring `StopWatch` instrumentation in `AIServiceImpl.suggestResolution()` — 3 segments: `contextRetrieval`, `llmCall`, `responseParser`. 20 synthetic tickets benchmarked via `POST /api/v1/ai/benchmark`.

| Metric | ms |
|---|---|
| Min total | 3 554 |
| Max total | 10 440 |
| Avg total | 5 614 |
| P50 total | 5 644 |
| P95 total | 7 016 |
| Avg contextRetrieval | 0 (RAG disabled, DB fallback) |
| Avg llmCall | 5 614 (hermes3:3b, CPU) |
| Avg responseParser | ~1 (negligible) |

> Full results: `measurements/pipeline_latency.json`
