set foreign_key_checks = 0;

delete
from product;
delete
from request_item;
delete
from request;
delete
from cooker;
delete
from payment_method;
delete
from restaurant_payment_method;
delete
from restaurant;
delete
from user_group;
delete
from group_permission;
delete
from user;
delete
from `group`;
delete
from permission;


set foreign_key_checks = 1;

alter table cooker
    auto_increment = 1;
alter table payment_method
    auto_increment = 1;
alter table restaurant
    auto_increment = 1;
alter table user
    auto_increment = 1;
alter table `group`
    auto_increment = 1;
alter table permission
    auto_increment = 1;
alter table product
    auto_increment = 1;
alter table request_item
    auto_increment = 1;
alter table request
    auto_increment = 1;

insert into payment_method (description, name, last_modification_at)
values ('cach', 'CACH', utc_timestamp),
       ('visa', 'VISA', utc_timestamp),
       ('paypal', 'PAYPAL', utc_timestamp)
;

insert into cooker (name, version)
values ('Mario Nabais', 0)
     , ('Alberto Chacal', 0)
     , ('Carlos Lisboa', 0);

insert into restaurant (name, take_away_tax, cooker_id, version, city, street, zip_code, created_at, updated_at)
values ('River House', 3.0, 1, 0, 'Soure', 'At the top of the bridge', '3130', '2019-01-02', '2019-01-02'),
       ('Quinta St. Maria', 6.0, 1, 0, 'Condeixa', 'Quinta St. Maria', '3030', '2019-01-02', '2019-01-02');

insert into restaurant_payment_method (restaurant_id, payment_method_id)
VALUES (1, 1)
     , (1, 2)
     , (2, 3)
;

insert into product (name, description, price, active, restaurant_id)
VALUES ('Francesinha', 'Francesinha à moda do Porto', 15.0, true, 1)
     , ('Hot dog', 'Hot dog Basic', 2.0, true, 1)
;

insert into `group` (name)
values ('ADMIN'),
       ('CHEF');

insert into user (name, email, pw, registration_at)
values ('dummy1', 'dummy1@dummy.io.moc', 'abc', '2019-01-02')
     , ('dummy2', 'dummy2@dummy.io.moc', 'abc', '2019-01-02')
;


insert into request
(id, code, status, restaurant_id, sub_total, take_away_tax, total_value, created_at, payment_method_id, user_client_id,
 delivery_address_street, delivery_address_city, delivery_address_zip_code, version, delivery_at, canceled_at,
 confirmed_at)
values (1, 'cf9a9dd5-b602-4d77-9914-69cbac80af73', 'CREATED', 1, 30.00, 3.00, 33.00, '2020-01-01 16:05:39', 1, 1,
        'nothing 1',
        'abc', '1234', 0, null, null, null)
     , (2, '04f15303-5944-422d-a3c8-68fec0455c96', 'CREATED', 1, 30.00, 3.00, 33.00, '2020-01-01 16:05:40', 1, 1, 'abc',
        'abc', '1234', 0, null, null, null)
     , (3, 'd895b0cb-ef00-4150-a9e9-4b1cdcf074d8', 'CREATED', 1, 30.00, 3.00, 33.00, '2020-01-01 16:05:41', 1, 1, 'abc',
        'abc', '1234', 0, null, null, null)
     , (4, '53abcbea-fb77-497a-a98a-ed9bdf9445e0', 'CREATED', 1, 30.00, 3.00, 33.00, '2020-01-01 16:05:42', 1, 1, 'abc',
        'abc', '1234', 0, null, null, null)
     , (5, '30fc7b0a-8b09-4f4c-986d-149ee4fa3845', 'CREATED', 1, 30.00, 3.00, 33.00, '2020-01-01 16:05:43', 1, 1, 'abc',
        'abc', '1234', 0, null, null, null);


insert into request_item (id, request_id, unit_price, total_price, qty, observations, product_id)
values (1, 1, 15.00, 30.00, 2, 'nothing', 1)
     , (2, 2, 15.00, 30.00, 2, 'nothing', 1)
     , (3, 3, 15.00, 30.00, 2, 'nothing', 1)
     , (4, 4, 15.00, 30.00, 2, 'nothing', 1)
     , (5, 5, 15.00, 30.00, 2, 'nothing', 1);