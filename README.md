# SocialSentiment

Учебный веб-сервис анализа реакции аудитории на Instagram-публикации.

## Технологии
- Java 21, Spring Boot 4, Maven, Spring Data JPA, PostgreSQL 16
- Python FastAPI, Scrapfly (сбор), XLM-RoBERTa (классификация)
- Next.js 16, React 19, TypeScript, Tailwind CSS — [frontend](https://github.com/RollSatrs/SocialSentiment-Frontend)

## Возможности
- Сбор доступных комментариев Instagram через Scrapfly (при наличии ключа)
- Анализ комментариев, вставленных вручную
- Классификация тональности negative / neutral / positive
- Статистика, фильтрация, поиск и история
- Повторная классификация сохранённых комментариев

## Быстрый старт: база данных + Java backend через Docker
Требуется Docker. Создайте `.env` по примеру `.env.example` и укажите пароль (не коммитьте его):
```sh
cp .env.example .env
docker compose up --build -d
```
Java API: http://localhost:4001. PostgreSQL в контейнере, локальный порт 5433. Для анализа моделью запустите Python parser отдельно на хосте (см. ниже). Если Python не запущен, запросы анализа в режиме `ai` не выполнятся.

## Локальный запуск
Нужны Java 21, PostgreSQL и Python с Poetry. PostgreSQL на localhost:5432 с БД `socialsentiment`, пароль через `DB_PASSWORD`:
```sh
export DB_PASSWORD='your-local-password'
./mvnw spring-boot:run
```
Parser, в отдельном терминале:
```sh
cd parser
poetry install --no-root
poetry run pip install -r requirements-api.txt
poetry run pip install -r requirements-sentiment.txt
export SCRAPFLY_KEY='your-private-key'
poetry run uvicorn api:app --host 127.0.0.1 --port 4002
```
Ключ Scrapfly нужен только для автоматического сбора. Модель при первом запуске может скачать веса. Подробности и лицензионные ограничения — `parser/README.md`.

Frontend:
```sh
git clone https://github.com/RollSatrs/SocialSentiment-Frontend.git
cd SocialSentiment-Frontend
pnpm install
pnpm dev
```
UI: http://localhost:4000.

## Проверки
```sh
./mvnw test
curl http://localhost:4001/api/v1/analyses
curl http://127.0.0.1:4002/health
```
Импортируйте `docs/SocialSentiment.postman_collection.json` в Postman для тестирования REST API.

## Документация
- [User Stories](docs/USER_STORIES.md)
- [ER-диаграмма DBML](docs/ER_DIAGRAM.md)
- [API-контракт](docs/API.md)
- [Архитектура](docs/ARCHITECTURE.md)
- [План тестирования](docs/TEST_PLAN.md)
- [Чеклист к защите](docs/REVIEW_CHECKLIST.md)

## Git workflow
Создавайте `feature/<name>` от `main`, коммитьте логические изменения, открывайте PR в `main`. До слияния проверяйте тесты и выполняйте ревью.

## Ограничения
Сбор комментариев зависит от стороннего сервиса и не гарантирован для всех публикаций. Доступа пользователей и авторизации нет. Это учебная локальная система, не готовая к публичному production. Код стороннего парсера имеет лицензионные ограничения, см. `parser/UPSTREAM_LICENSE.txt`.

Скриншоты интерфейса нужно сделать после запуска и положить в `docs/screenshots/`; изображения не подменяются макетами.
