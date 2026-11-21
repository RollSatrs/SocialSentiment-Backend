# Architecture — SocialSentiment

```text
Next.js 16 (port 4000)
       |
       v HTTP JSON
Spring Boot 4 / Java 21 (port 4001)
       |
       +--> InstagramAnalysisController (REST + DTO)
       |       |
       |       v
       |   AnalysisService (бизнес-логика)
       |       +--> InstagramUrlService (проверка URL)
       |       +--> PythonInstagramService (сбор через Scrapfly)
       |       +--> InstagramService (другой источник)
       |       +--> AiSentimentService (модель через Python API)
       |       +--> SentimentService (словарный fallback/режим)
       |       +--> AnalysisRepository (JPA)
       |                |
       |                v
       |            PostgreSQL
       |
Python FastAPI (port 4002): Instagram parser + multilingual sentiment model
```

- Контроллер возвращает `AnalysisDto.Response`, не JPA Entity.
- `ApiExceptionHandler` и `InstagramApiExceptionHandler` централизуют обработку ошибок; детали следует проверить для всех внешних сбоев.
- PostgreSQL credentials через `DB_PASSWORD`; ключ Scrapfly должен передаваться через окружение, не коммититься.
- Режим классификации зависит от конфигурации backend; наличие AI-модуля не означает, что каждый запрос использует его.
- `spring.jpa.hibernate.ddl-auto=update` подходит для разработки, но не для production: миграции ещё нужны.
- Нет авторизации и приватного разделения истории: не размещать публично без доработки.
- Фронтенд показывает ориентировочные этапы загрузки; backend пока не передаёт real-time progress.
