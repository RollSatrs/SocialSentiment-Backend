# REST API Contract

Base URL: `http://localhost:4001`

## POST /api/v1/analyses

Request:
```json
{
  "url": "https://www.instagram.com/p/ABC123/",
  "comments": ["Ужасно", "Отлично", "Когда новая публикация?"]
}
```

Response: `201 Created` (example; UUID and timestamp vary)
```json
{
  "id": "d14d59d5-6d27-41f8-87ab-993245e9c77c",
  "status": "COMPLETED",
  "url": "https://www.instagram.com/p/ABC123/",
  "source": "user_supplied",
  "message": "Анализ предоставленных комментариев выполнен словарным классификатором; это не AI-модель.",
  "total": 3,
  "negative": 1,
  "neutral": 1,
  "positive": 1,
  "createdAt": "2026-11-20T18:00:00Z",
  "comments": [
    {"text": "Ужасно", "sentiment": "negative"},
    {"text": "Отлично", "sentiment": "positive"},
    {"text": "Когда новая публикация?", "sentiment": "neutral"}
  ]
}
```

Without comments, status is `COLLECTOR_NOT_CONNECTED` and counts are zero. No fake comments are returned.

## GET /api/v1/analyses

Returns up to 20 recent analyses, newest first.

## GET /api/v1/analyses/{id}

Returns one saved analysis; `404` if missing.

## Errors

`400`: `{"error":"INVALID_INPUT","message":"..."}`

`404`: `{"error":"NOT_FOUND","message":"Анализ не найден."}`

Validation: HTTPS Instagram post/Reel URL, up to 500 comments, up to 2000 characters each.
