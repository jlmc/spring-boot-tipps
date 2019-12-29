create table if not exists product
(
    id bigint auto_increment,
    name varchar(254) not null,
    description varchar(2048) null,
    price decimal(19,2) not null default 0.0,
    active boolean not null default true,
    restaurant_id bigint not null,
    primary key (id),
    constraint fk_product_restaurant_id foreign key (restaurant_id) references restaurant (id),
    index product_restaurant_id_index (restaurant_id)
);