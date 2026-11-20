# ER-диаграмма

Вставьте следующий код в https://dbdiagram.io для получения схемы.

```dbml
Table analyses {
  id uuid [pk]
  post_url varchar(500) [not null]
  status varchar(32) [not null]
  source varchar(32) [not null]
  created_at timestamp [not null]
}

Table comments {
  id uuid [pk]
  analysis_id uuid [not null, ref: > analyses.id]
  text varchar(2000) [not null]
  sentiment varchar(16) [not null]
}
```

**Связь:** analyses (1) → comments (N). Одна запись анализа содержит много комментариев; каждый комментарий относится ровно к одному анализу. Many-to-Many для текущих сценариев не требуется.

**Статусы:** `COMPLETED`, `COLLECTOR_NOT_CONNECTED`.
**Источники:** `user_supplied`, `none`.
