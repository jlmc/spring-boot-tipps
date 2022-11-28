create table if not exists request
(
    id                bigint auto_increment
        primary key,
    code              varchar(100)                not null,
    status            varchar(50)                 not null,
    restaurant_id     bigint                      not null,
    sub_total         decimal(19, 2) default 0.00 not null,
    take_away_tax     decimal(19, 2) default 0.00 not null,
    total_value       decimal(19, 2) default 0.00 not null,
    created_at        timestamp                   not null,
    payment_method_id bigint                      not null,
    user_client_id    bigint                      not null,
    constraint code
        unique (code),
    constraint fk_request_payment_method_id
        foreign key (payment_method_id) references payment_method (id),
    constraint fk_request_restaurant_id
        foreign key (restaurant_id) references restaurant (id),
    constraint fk_request_user_client_id_user_id
        foreign key (user_client_id) references user (id)
);

create table request_item
(
    id           bigint auto_increment
        primary key,
    request_id   bigint                      not null,
    product_id   bigint                      not null,
    unit_price   decimal(19, 2) default 0.00 not null,
    total_price  decimal(19, 2) default 0.00 not null,
    qty          int            default 1    not null,
    observations varchar(2048)               null,
    constraint fk_request_item_request_id
        foreign key (request_id) references request (id),
    constraint  fk_request_item_product_id
        foreign key (product_id) references product(id),
    index request_item_request_id_index (request_id)
);
