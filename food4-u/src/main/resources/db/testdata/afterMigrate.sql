set foreign_key_checks = 0;

delete from cooker;
delete from payment_method;
delete from restaurant_payment_method;
delete from restaurant;

set foreign_key_checks = 1;

alter table cooker auto_increment = 1;
alter table payment_method auto_increment = 1;
alter table restaurant auto_increment = 1;

insert into cooker (name, version) value ('Mario Nabais', 0);