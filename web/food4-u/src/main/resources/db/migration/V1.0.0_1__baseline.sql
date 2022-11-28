--create schema food4u collate utf8mb4_0900_ai_ci;

create table cooker
(
    id bigint auto_increment
        primary key,
    name varchar(255) not null,
    version int not null
);

create table payment_method
(
    id bigint auto_increment
        primary key,
    description varchar(255) null,
    name varchar(255) null
);

create table restaurant
(
    id bigint auto_increment
        primary key,
    name varchar(255) not null,
    take_away_tax decimal(19,2) not null,
    cooker_id bigint not null,
    version int not null,
    city varchar(255) null,
    street varchar(255) null,
    zip_code varchar(255) null,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    constraint FK_cooker_id
        foreign key (cooker_id) references cooker (id)
);

create table restaurant_payment_method
(
    restaurant_id bigint not null,
    payment_method_id bigint not null,
    primary key (restaurant_id, payment_method_id),
    constraint FK_payment_method_id
        foreign key (payment_method_id) references payment_method (id),
    constraint FK_restaurant_id
        foreign key (restaurant_id) references restaurant (id)
);

