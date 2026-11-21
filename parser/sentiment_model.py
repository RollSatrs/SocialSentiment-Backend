"""Lazy-loaded multilingual sentiment model. First request downloads model weights."""
from functools import lru_cache

MODEL_ID = "cardiffnlp/twitter-xlm-roberta-base-sentiment"

@lru_cache(maxsize=1)
def get_pipeline():
    from transformers import pipeline
    return pipeline("text-classification", model=MODEL_ID, tokenizer=MODEL_ID)

def classify_batch(texts: list[str]) -> list[dict]:
    model = get_pipeline()
    results = []
    for start in range(0, len(texts), 16):
        batch = texts[start:start + 16]
        predictions = model(batch, truncation=True, max_length=512, batch_size=8)
        for prediction in predictions:
            label = prediction["label"].lower()
            if label not in {"negative", "neutral", "positive"}:
                raise ValueError("Unexpected model label")
            results.append({"sentiment": label, "confidence": round(float(prediction["score"]), 4)})
    return results
