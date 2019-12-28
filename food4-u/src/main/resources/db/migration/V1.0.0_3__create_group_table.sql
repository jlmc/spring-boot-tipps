drop table if exists `group_permission` cascade;
drop table if exists `user_group` cascade;
drop table if exists user cascade;
drop table if exists `group` cascade;
drop table if exists permission cascade;

create table if not exists user
(
    id    bigint auto_increment,
    name  varchar(255) not null,
    email varchar(255) not null,
    pw    varchar(2048),
    constraint user_pk primary key (id)
);

create table if not exists `group`
(
    id   bigint auto_increment primary key,
    name varchar(100) not null unique
);

create table if not exists user_group
(
    user_id  bigint not null,
    group_id bigint not null,

    primary key (user_id, group_id),
    constraint fk_user_group_user_id foreign key (`user_id`) references `user` (`id`),
    constraint fk_user_group_group_id foreign key (`group_id`) references `group` (`id`),

    index user_group_user_id (user_id),
    index user_group_group_id (group_id)
);

create table if not exists permission
(
    id   bigint auto_increment,
    name varchar(100) not null unique,
    primary key (id)
);

create table if not exists group_permission
(
    group_id      bigint not null,
    permission_id bigint not null,
    primary key (group_id, permission_id),
    constraint fk_group_permission_group_id foreign key (group_id) references `group` (id),
    constraint fk_group_permission_permission_id foreign key (permission_id) references permission (id),
    index group_permission_group_id_index (group_id),
    index group_permission_permission_id_index (permission_id)
);

-- alter table group_permission add index group_permission_permission_id_index (group_id)
