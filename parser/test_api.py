from fastapi.testclient import TestClient

from api import app, valid_post_url

client = TestClient(app)


def test_health(monkeypatch):
    monkeypatch.delenv("SCRAPFLY_KEY", raising=False)
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok", "configured": False}


def test_invalid_url():
    for url in ["http://www.instagram.com/p/ABC/", "https://evil.example/p/ABC/", "https://www.instagram.com/"]:
        assert not valid_post_url(url)
        assert client.post("/collect", json={"url": url}).status_code == 400


def test_requires_key(monkeypatch):
    monkeypatch.delenv("SCRAPFLY_KEY", raising=False)
    response = client.post("/collect", json={"url": "https://www.instagram.com/p/ABC123/"})
    assert response.status_code == 503
