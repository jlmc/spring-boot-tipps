alter table t_posts add column if not exists created_date timestamp not null default current_timestamp;
alter table t_posts add column if not exists last_modified_date timestamp not null default current_timestamp;
alter table t_posts add column if not exists src_system varchar not null default '';
alter table t_posts add column if not exists votes integer not null default 0;
