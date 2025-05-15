"""
FastAPI RAG retrieval service backed by ChromaDB.
Exposes POST /retrieve to return top-K semantically similar blockage records.

Usage:
    cd ai-module
    python rag_service.py
    # Listens on http://localhost:8001

Set rag.enabled=true in Spring application.properties to activate.
"""

import os
import chromadb
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import uvicorn

CHROMA_PATH = os.path.join(os.path.dirname(__file__), "chroma_db")
COLLECTION_NAME = "blockage_history"

app = FastAPI(title="TickIT RAG Service", version="1.0.0")

print("Loading embedding model...")
_model = SentenceTransformer("all-MiniLM-L6-v2")

print("Connecting to ChromaDB...")
_client = chromadb.PersistentClient(path=CHROMA_PATH)
_collection = _client.get_collection(COLLECTION_NAME)
print(f"Collection loaded: {_collection.count()} records.")


class RetrieveRequest(BaseModel):
    query: str
    top_k: int = 3


class RetrieveResponse(BaseModel):
    results: list
    query: str
    top_k: int


@app.post("/retrieve", response_model=RetrieveResponse)
def retrieve(req: RetrieveRequest):
    if not req.query or not req.query.strip():
        raise HTTPException(status_code=400, detail="query must not be empty")

    query_embedding = _model.encode([req.query]).tolist()[0]

    results = _collection.query(
        query_embeddings=[query_embedding],
        n_results=min(req.top_k, _collection.count()),
        include=["metadatas", "distances", "documents"],
    )

    items = []
    if results and results["metadatas"] and results["metadatas"][0]:
        for meta, dist, doc in zip(
            results["metadatas"][0],
            results["distances"][0],
            results["documents"][0],
        ):
            items.append({
                "blockage_type": meta.get("blockage_type", "unknown"),
                "description": meta.get("description", ""),
                "resolution_steps": meta.get("resolution_steps", ""),
                "duration_hours": meta.get("duration_hours", 0),
                "outcome": meta.get("outcome", "unknown"),
                "distance": round(dist, 4),
            })

    return RetrieveResponse(results=items, query=req.query, top_k=req.top_k)


@app.get("/health")
def health():
    return {"status": "ok", "records": _collection.count()}


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001, log_level="info")
