# Режимы запуска

## Docker: PostgreSQL + Spring Boot
`cp .env.example .env && docker compose up --build -d`

По умолчанию `APP_SENTIMENT_MODE=dictionary`, поэтому ручной ввод работает без Python. Автоматический сбор Scrapfly и повторный AI-анализ требуют Python parser.

## Локальный полный стек
Для полного режима запустите Python FastAPI на порту 4002, PostgreSQL, Spring Boot и Next.js. Если Java запущена **в Docker**, Python должен быть доступен контейнеру через `host.docker.internal:4002`; привязка Python только к `127.0.0.1` хоста для этого недостаточна. Используйте отдельную защищённую локальную сеть или запустите Java непосредственно на хосте. Не открывайте parser публично без авторизации и защиты.

## Важно
Docker Compose в репозитории контейнеризует **PostgreSQL и Java**, не Python и frontend. Поэтому он не является полным one-command развёртыванием всех компонентов. Для учебного ревью это явно обозначенное ограничение.
