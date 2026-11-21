# API Contract — SocialSentiment

Base URL: `http://localhost:4001`; JSON. Внешние вызовы к Python parser выполняет Java backend.

## POST /api/v1/analyses — 201 Created

Автоматический сбор:
```json
{"url":"https://www.instagram.com/p/ABC123/","source":"scrapfly"}
```
Ручные комментарии:
```json
{"url":"https://www.instagram.com/p/ABC123/","comments":["Ужасно","Отлично","Когда новый пост?"]}
```
Пример структуры успешного ответа (значения классификации иллюстративны):
```json
{
  "id":"d14d59d5-6d27-41f8-87ab-993245e9c77c",
  "status":"COMPLETED",
  "url":"https://www.instagram.com/p/ABC123/",
  "source":"user_supplied",
  "message":"Анализ завершён",
  "total":3,"negative":1,"neutral":1,"positive":1,
  "createdAt":"2026-11-20T18:00:00Z",
  "comments":[
    {"text":"Ужасно","sentiment":"negative"},
    {"text":"Отлично","sentiment":"positive"},
    {"text":"Когда новый пост?","sentiment":"neutral"}
  ]
}
```

## GET /api/v1/analyses — 200 OK
Массив до 20 последних анализов, сортировка по дате (новые сначала).

## GET /api/v1/analyses/{id} — 200 OK
Один сохранённый отчёт в формате ответа POST. Если ID не найден — 404.

## POST /api/v1/analyses/{id}/reanalyze — 201 Created
Повторно классифицирует уже сохранённые комментарии моделью, без нового обращения к Instagram. Создаёт новый отчёт с `source: "reanalyzed_ai"`.

## Ошибки
- `400` — некорректный URL или запрос, например `{"error":"INVALID_INPUT","message":"..."}`.
- `404` — анализ не найден, например `{"error":"NOT_FOUND","message":"Анализ не найден."}`.
- Ошибки стороннего сервиса/парсера могут возвращать другие статусы и сообщения; их следует проверить отдельно.

Ограничения DTO: максимум 500 комментариев, URL должен соответствовать Instagram post/Reel; длина текста комментария ограничивается хранилищем.

Для ручной проверки используйте `docs/SocialSentiment.postman_collection.json`. Сверьте коллекцию с эндпоинтом reanalyze перед сдачей.
