# ER-диаграмма SocialSentiment

Вставьте DBML в https://dbdiagram.io и экспортируйте PNG для защиты.

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

**Связь:** `analyses 1:N comments`. В Java: `AnalysisEntity.comments` — `@OneToMany`; `CommentEntity.analysis` — `@ManyToOne`. Many-to-Many не требуется.

**Источники:** `user_supplied`, `scrapfly`, `instagram_api`, `reanalyzed_ai` (зависят от пути выполнения). Тональности: `negative`, `neutral`, `positive`. Состояния и сообщения об ошибках зависят от способа анализа.

Диаграмма отражает JPA-сущности текущего проекта; конкретные SQL-типы времени зависят от Hibernate/PostgreSQL.
