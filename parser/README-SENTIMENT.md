# Multilingual AI sentiment

Install in backend/parser (inside Poetry virtualenv):

```bash
poetry run python -m pip install -r requirements-sentiment.txt
```

Restart FastAPI on port 4002. The first POST /sentiment downloads the CardiffNLP XLM-RoBERTa model and can take time and disk space. Model supports multilingual text but quality varies by language and domain; validate with human-labelled examples.

Spring Boot defaults to app.sentiment.mode=ai and sends batches to POST /sentiment. To temporarily use the original dictionary classifier, start Java with environment variable APP_SENTIMENT_MODE=dictionary (Spring relaxed binding) or set app.sentiment.mode=dictionary in application.properties. Old saved analyses are not automatically reclassified.

Example local test:
```bash
curl -X POST http://127.0.0.1:4002/sentiment -H 'Content-Type: application/json' -d '{"texts":["Отвратительное место","Спасибо, очень интересно!","Когда выйдет видео?"]}'
```
