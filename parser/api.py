"""HTTP adapter for Scrapfly's Instagram comments example (non-commercial educational use)."""
import os
import logging
from urllib.parse import urlparse

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

logger = logging.getLogger(__name__)

app = FastAPI(title="SocialSentiment Instagram Parser", version="0.1.0")


class CollectRequest(BaseModel):
    url: str


def valid_post_url(url: str) -> bool:
    try:
        parsed = urlparse(url)
        path_parts = parsed.path.strip("/").split("/")
        return (
            parsed.scheme == "https"
            and parsed.hostname in {"instagram.com", "www.instagram.com"}
            and parsed.port is None
            and parsed.username is None
            and parsed.password is None
            and len(path_parts) == 2
            and path_parts[0] in {"p", "reel", "tv"}
            and bool(path_parts[1])
            and all(c.isalnum() or c in "_-" for c in path_parts[1])
        )
    except ValueError:
        return False


@app.get("/health")
def health():
    return {"status": "ok", "configured": bool(os.getenv("SCRAPFLY_KEY"))}


@app.post("/collect")
async def collect(body: CollectRequest):
    if not valid_post_url(body.url):
        raise HTTPException(status_code=400, detail="Invalid Instagram post URL")
    if not os.getenv("SCRAPFLY_KEY"):
        raise HTTPException(status_code=503, detail="SCRAPFLY_KEY is not configured")

    # Lazy import avoids initializing the SDK before configuration and keeps health independent.
    from instagram import scrape_post_comments

    try:
        comments = await scrape_post_comments(body.url)
    except Exception:
        # Log the failure server-side; keep upstream details out of API responses.
        logger.exception("Instagram collection failed")
        raise HTTPException(status_code=502, detail="Instagram collection failed")

    texts = [
        item.get("text", "").strip()
        for item in comments
        if isinstance(item, dict) and isinstance(item.get("text"), str) and item.get("text", "").strip()
    ]
    return {"source": "scrapfly", "comments": texts[:500], "count": min(len(texts), 500)}
