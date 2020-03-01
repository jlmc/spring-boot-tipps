-- all the passwords are 'pwd' hashed by https://bcrypt-generator.com/
insert into t_user (id, pw, name, email, registration_at) values (1, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'john', 'john@test.com', current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at) values (2, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'maria', 'maria@test.com', current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at) values (3, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'foo', 'foo@test.com', current_timestamp(6) );


insert into t_permission (id, name, description) values (1, 'MANAGER_USERS', 'Manager Users');
insert into t_permission (id, name, description) values (2, 'SEE_BOOKS', 'See Books');
insert into t_permission (id, name, description) values (3, 'EDIT_BOOKS', 'Edit Books');
insert into t_permission (id, name, description) values (4, 'CREATE_ORDER', 'Create Order');


insert into t_user_group(id, name) values (1, 'Administrator');
insert into t_user_group(id, name) values (2, 'Seller');
insert into t_user_group(id, name) values (3, 'Buyer');


insert into t_user_group_permission (user_group_id, permission_id) values (1, 1);
insert into t_user_group_permission (user_group_id, permission_id) values (1, 2);
insert into t_user_group_permission (user_group_id, permission_id) values (1, 3);
insert into t_user_group_permission (user_group_id, permission_id) values (1, 4);
insert into t_user_group_permission (user_group_id, permission_id) values (2, 2);
insert into t_user_group_permission (user_group_id, permission_id) values (2, 3);
insert into t_user_group_permission (user_group_id, permission_id) values (2, 4);
insert into t_user_group_permission (user_group_id, permission_id) values (3, 2);
insert into t_user_group_permission (user_group_id, permission_id) values (3, 4);


insert into t_user_user_group(user_id, user_group_id) values (1, 1);
insert into t_user_user_group(user_id, user_group_id) values (1, 2);
insert into t_user_user_group(user_id, user_group_id) values (1, 3);
insert into t_user_user_group(user_id, user_group_id) values (2, 2);
insert into t_user_user_group(user_id, user_group_id) values (3, 3);