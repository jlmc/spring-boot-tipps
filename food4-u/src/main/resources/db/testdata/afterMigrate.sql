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

insert into payment_method (description, name)
values ('cach', 'CACH'),
       ('visa', 'VISA'),
       ('paypal', 'PAYPAL')
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