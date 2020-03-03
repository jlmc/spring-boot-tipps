insert into t_user (id, pwd, username, email) values (1, 'pwd', 'john', 'john@test.com');
insert into t_user (id, pwd, username, email) values (2, 'pwd', 'maria', 'maria@test.com');
insert into t_user (id, pwd, username, email) values (3, 'pwd', 'foo', 'foo@test.com');
insert into t_user_permission (user_id, permissions) values (1, 'ADMIN');
insert into t_user_permission (user_id, permissions) values (1, 'CLIENT');
insert into t_user_permission (user_id, permissions) values (2, 'ADMIN');
insert into t_user_permission (user_id, permissions) values (3, 'CLIENT');

insert into t_book (id, isbn, title, author) values (1, 'abc-1', 'Real World Java EE Patterns', 1);
insert into t_book (id, isbn, title, author) values (2, 'abc-2', 'Building Restful Web Services with Java EE 8', 1);
insert into t_book (id, isbn, title, author) values (3, 'abc-3', 'Docker in Action', 2);
insert into t_book (id, isbn, title, author) values (4, 'abc-4', 'Kafka the definitive guide', 1);

insert into t_shopping_cart (user_id) values ( 1 );

insert into t_shopping_cart_item(id, shopping_cart_id, book_id, qty) values (1, 1, 1, 2);
insert into t_shopping_cart_item(id, shopping_cart_id, book_id, qty) values (2, 1, 2, 2);