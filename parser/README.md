# SocialSentiment Instagram parser

The Instagram collection implementation originates from [Scrapfly's instagram-scraper example](https://github.com/scrapfly/scrapfly-scrapers/tree/main/instagram-scraper), with upstream code in `instagram.py` and its original license in `UPSTREAM_LICENSE.txt`.

**Important:** Scrapfly's code and service are not the same thing: the example requires an API key and consumes Scrapfly credits. Scraping third-party comments is best-effort, can fail, and must respect Instagram's and Scrapfly's terms. Upstream's Non-Profit Open Software License (NPOSL-3.0) may restrict commercial deployment.

## Install (from backend/parser)

```sh
poetry install --no-root
poetry run pip install -r requirements-api.txt
```

Set the secret only in the terminal session (do not commit it):
```sh
export SCRAPFLY_KEY='YOUR_PRIVATE_KEY'
poetry run uvicorn api:app --host 127.0.0.1 --port 4002
```

The API listens only on localhost. The `/health` endpoint checks startup without spending credits. The `/collect` endpoint makes a real upstream request and may use credits.

## Java integration

Start Spring Boot on port 4001. It connects to Python on `http://127.0.0.1:4002` (overridable via `APP_PARSER_BASE_URL`/Spring property `app.parser.base-url`; the default is localhost).

```sh
curl -X POST http://localhost:4001/api/v1/analyses \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://www.instagram.com/p/REPLACE_WITH_POST_CODE/","source":"scrapfly"}'
```

* `source: "scrapfly"` -> calls Python -> Scrapfly.
* `source: "instagram_api"` or omitted -> existing Meta Graph API (own authorized media only).
* `comments: ["...", "..."]` -> manual analysis, no upstream requests.

The Next.js frontend must explicitly pass `source: "scrapfly"` for the Python collector. This backend change does **not** change frontend defaults.

For a no-credit smoke test: `curl http://127.0.0.1:4002/health`. The legacy `run.py` script issues multiple paid-credit requests, so avoid running it merely to check installation.
