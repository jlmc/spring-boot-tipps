insert into t_user (id, pwd, username, email) values (1, 'pwd', 'john', 'john@test.com');
insert into t_user (id, pwd, username, email) values (2, 'pwd', 'maria', 'maria@test.com');
insert into t_user (id, pwd, username, email) values (3, 'pwd', 'foo', 'foo@test.com');
insert into t_user_permission (user_id, permissions) values (1, 'ADMIN');
insert into t_user_permission (user_id, permissions) values (1, 'CLIENT');
insert into t_user_permission (user_id, permissions) values (2, 'ADMIN');
insert into t_user_permission (user_id, permissions) values (3, 'CLIENT');

insert into t_book (id, isbn, title) values (1, 'abc-1', 'Real World Java EE Patterns');
insert into t_book (id, isbn, title) values (2, 'abc-2', 'Building Restful Web Services with Java EE 8');
insert into t_book (id, isbn, title) values (3, 'abc-3', 'Docker in Action');
insert into t_book (id, isbn, title) values (4, 'abc-4', 'Kafka the definitive guide');