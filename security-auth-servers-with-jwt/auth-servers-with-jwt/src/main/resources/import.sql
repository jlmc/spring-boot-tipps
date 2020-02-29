-- all the passwords are 'pwd' hashed by https://bcrypt-generator.com/
insert into t_user (id, pw, name, email, registration_at) values (1, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'john', 'john@test.com', current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at) values (2, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'maria', 'maria@test.com', current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at) values (3, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'foo', 'foo@test.com', current_timestamp(6) );
--insert into t_user_permission (user_id, permissions) values (1, 'ADMIN');
--insert into t_user_permission (user_id, permissions) values (1, 'CLIENT');
--insert into t_user_permission (user_id, permissions) values (2, 'ADMIN');
--insert into t_user_permission (user_id, permissions) values (3, 'CLIENT');

--insert into t_book (id, isbn, title) values (1, 'abc-1', 'Real World Java EE Patterns');
--insert into t_book (id, isbn, title) values (2, 'abc-2', 'Building Restful Web Services with Java EE 8');
--insert into t_book (id, isbn, title) values (3, 'abc-3', 'Docker in Action');
--insert into t_book (id, isbn, title) values (4, 'abc-4', 'Kafka the definitive guide');