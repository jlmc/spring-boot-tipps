create table if not exists t_comments
(
    id   uuid not null primary key,
    text varchar      not null,
    post_id uuid not null references t_posts(id),
    author_id uuid not null references t_users(id)
);
