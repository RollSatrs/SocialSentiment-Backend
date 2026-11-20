# Architecture and review notes

Frontend: Next.js 16, React, TypeScript, Tailwind, port 4000.
Backend: Java 21, Spring Boot, REST, JPA, port 4001.
Database: PostgreSQL, port 5432.

Request path:
`Next.js page → InstagramAnalysisController → AnalysisService → InstagramUrlService / SentimentService → AnalysisRepository → PostgreSQL`

Entities are never returned directly from controllers: `AnalysisDto` records form the HTTP contract.
`ApiExceptionHandler` centralizes 400/404 responses. Spring Boot uses SLF4J/Logback.
Database password is read from `DB_PASSWORD` environment variable.

### Limitations / next iteration
- Instagram collection module: not yet implemented; do not claim arbitrary post access.
- Basic word-list sentiment classifier, not machine learning.
- No user accounts; history is shared locally.
- `ddl-auto=update` for development only; use Flyway migrations before production.
- No deployment or public production security hardening.
- Integration smoke test: run backend and issue POST/GET requests described in `docs/API.md`.
