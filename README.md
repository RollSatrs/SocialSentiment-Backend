# SocialSentiment — Java Backend

Java 21 / Spring Boot 4 / PostgreSQL. Analyzes sentiment of **user-supplied Instagram comments**, persists analyses, exposes REST API.

**Important:** Instagram automatic comment collector is not connected. No comments are fabricated.

## Run

1. Create PostgreSQL database: `CREATE DATABASE socialsentiment;`
2. Set `DB_PASSWORD` to your local PostgreSQL password.
3. Run `mvn spring-boot:run`. Backend: http://localhost:4001.

macOS:
```bash
export DB_PASSWORD='your-local-password'
mvn spring-boot:run
```

## Tests

```bash
mvn test
```

## Documentation

- [User stories](docs/USER_STORIES.md)
- [ER diagram (dbdiagram.io)](docs/ER_DIAGRAM.md)
- [API contract and examples](docs/API.md)
- [Architecture and limitations](docs/ARCHITECTURE.md)

Frontend: https://github.com/RollSatrs/SocialSentiment-Frontend

For coursework review: changes are committed with descriptive messages. A feature-branch PR is still required for the next reviewed feature; this initial MVP was committed directly to main at the project owner's request.
