create table if not exists t_users
(
    id
    uuid
    not
    null
    primary
    key,
    name
    varchar
    not
    null,
    notes
    varchar
);


create table if not exists t_posts
(
    id          uuid not null primary key,
    title       varchar      not null,
    description varchar      not null,
    author_id   uuid references t_users (id)
);
