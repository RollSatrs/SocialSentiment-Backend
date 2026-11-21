# Instagram parser (experimental)

This directory contains the `instagram-scraper` example from https://github.com/scrapfly/scrapfly-scrapers (upstream: Scrapfly). The original source files are preserved, with the upstream license copied to `UPSTREAM_LICENSE.txt`.

**Important:** This is not a standalone free Instagram client. It requires `SCRAPFLY_KEY` and consumes Scrapfly API credits, including when running tests. The source repository is published under the Non-Profit Open Software License 3.0. Check license compatibility and service/Instagram terms before using or distributing beyond this educational prototype.

## Run locally

Requires Python 3.12+, Poetry, a valid Scrapfly key and any required provider credits.

```sh
cd parser
export SCRAPFLY_KEY='YOUR_KEY'
poetry install --no-root
poetry run python run.py
```

For the focused comments test, use `poetry run pytest test.py -k test_post_comments_scraping` (this also uses credits). The example runner scrapes multiple Instagram endpoints and saves sample output in `results/`.

**Integration status:** Files are vendored as examples only; the Java Spring Boot API does not yet call this parser. Do not commit API keys.
