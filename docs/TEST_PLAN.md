# Test plan

## Unit tests (JUnit 5 + Mockito)
- Negative text classification.
- Positive text classification.
- Neutral text classification.
- English negative text classification.
- Empty text validation.
- Analysis persistence with counts.
- No fabricated comments when collector unavailable.
- Invalid Instagram URL.
- Missing analysis ID.

Run: `mvn test`

## API smoke test
Start backend on port 4001, then:
```bash
curl -i -X POST http://localhost:4001/api/v1/analyses \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://www.instagram.com/p/ABC123/","comments":["Ужасно","Отлично","Когда?"]}'
curl -i http://localhost:4001/api/v1/analyses
```

Expected: HTTP 201 for POST, 200 for GET, total=3, one item per sentiment.
Import `SocialSentiment.postman_collection.json` into Postman to run manually.

**Status:** test code and collection are committed; local test execution is required before claiming pass.
