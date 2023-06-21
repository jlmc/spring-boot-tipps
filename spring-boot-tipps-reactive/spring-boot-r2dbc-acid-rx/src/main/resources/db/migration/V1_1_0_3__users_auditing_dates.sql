alter table t_users
    add column if not exists created_date timestamp not null default current_timestamp;
alter table t_users
    add column if not exists last_modified_date timestamp not null default current_timestamp;
alter table t_users
    add column if not exists version integer not null default 0;
