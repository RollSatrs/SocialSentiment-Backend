# SocialSentiment

Учебный веб-сервис анализа реакции аудитории на Instagram-публикации.

## Стек
- Java 21, Spring Boot 4, Maven, Spring Data JPA
- PostgreSQL
- Python FastAPI, Scrapfly и XLM-RoBERTa
- Next.js 16, React 19, TypeScript, Tailwind CSS — [frontend](https://github.com/RollSatrs/SocialSentiment-Frontend)

## Возможности
- Сбор доступных комментариев Instagram через Scrapfly (при наличии ключа)
- Ручной ввод комментариев
- Анализ тональности negative / neutral / positive
- Статистика, фильтры, поиск и история
- Повторный анализ сохранённых комментариев

## Локальный запуск

Требуются Java 21, PostgreSQL, Python с Poetry и pnpm. Создайте локальную БД `socialsentiment` в PostgreSQL.

**1. Python parser** (из папки `parser`):
```sh
poetry install --no-root
poetry run pip install -r requirements-api.txt
poetry run pip install -r requirements-sentiment.txt
export SCRAPFLY_KEY='your-private-key'
poetry run uvicorn api:app --host 127.0.0.1 --port 4002
```
Ключ Scrapfly требуется только для автоматического сбора. Модель может скачивать веса при первом запуске.

**2. Spring Boot backend** (из корня backend, в другом терминале):
```sh
export DB_PASSWORD='your-local-postgres-password'
./mvnw spring-boot:run
```
Backend: http://localhost:4001. PostgreSQL по умолчанию: localhost:5432.

**3. Next.js frontend** (в отдельном терминале):
```sh
git clone https://github.com/RollSatrs/SocialSentiment-Frontend.git
cd SocialSentiment-Frontend
pnpm install
pnpm dev
```
Frontend: http://localhost:4000.

## Проверки
```sh
./mvnw test
curl http://localhost:4001/api/v1/analyses
curl http://127.0.0.1:4002/health
```
Postman-коллекция: `docs/SocialSentiment.postman_collection.json`.

## Документация
- [User Stories](docs/USER_STORIES.md)
- [ER-диаграмма](docs/ER_DIAGRAM.md)
- [API-контракт](docs/API.md)
- [Архитектура](docs/ARCHITECTURE.md)
- [Тест-план](docs/TEST_PLAN.md)
- [Чеклист ревью](docs/REVIEW_CHECKLIST.md)

## Git workflow
Новый функционал — в `feature/<name>` и через PR в `main`. Перед слиянием выполняются тесты и ревью.

## Ограничения
Сбор комментариев зависит от стороннего сервиса. Авторизация отсутствует; проект предназначен для локального учебного запуска. Код стороннего парсера имеет лицензионные ограничения, см. `parser/UPSTREAM_LICENSE.txt`. Скриншоты следует снять с реально запущенного приложения.
