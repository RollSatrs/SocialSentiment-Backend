# Тестирование SocialSentiment

## Unit tests
В `src/test/java/com/socialsentiment/backend/analysis/` находятся JUnit/Mockito тесты бизнес-логики:
- Сохранение комментариев и подсчёт тональностей
- Маршрутизация запроса через Scrapfly
- Маршрутизация через Meta Graph API
- Повторная классификация сохранённых комментариев
- Некорректный URL
- Несуществующий ID

Также есть `SentimentServiceTest` и тест загрузки Spring context.

Запуск:
```sh
mvn test
```

## Проверка REST API
Импортируйте `SocialSentiment.postman_collection.json` в Postman, затем выполните запросы по порядку:
1. Create manual analysis — 201, сохраняет ID
2. List recent — 200, массив
3. Get created analysis — 200, тот же ID
4. Reanalyze — 201, нужен запущенный Python sentiment endpoint
5. Invalid URL — 400

Автоматический Scrapfly тест отдельно, чтобы не тратить кредиты без необходимости.

## Проверка интеграции
- PostgreSQL поднят и доступен Java
- Python `GET /health` возвращает 200
- Java POST сохраняет анализ в PostgreSQL
- Frontend отображает отчёт и историю

## Статус
Тестовые файлы и Postman-коллекция подготовлены. Успешность прогонов должна быть подтверждена логами локального запуска или GitHub Actions; наличие файлов не означает прохождение тестов.
