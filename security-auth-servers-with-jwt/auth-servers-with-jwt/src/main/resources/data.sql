-- all the passwords are 'pwd' hashed by https://bcrypt-generator.com/
insert into t_user (id, pw, name, email, registration_at)
values (1, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'john', 'john@test.com',
        current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at)
values (2, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'maria', 'maria@test.com',
        current_timestamp(6));
insert into t_user (id, pw, name, email, registration_at)
values (3, '$2y$12$KSGdjOJiBXjiDAecr.tzN.nRRDapuicLur6lZ9siieZzkggc/NrJe', 'foo', 'foo@test.com', current_timestamp(6));


insert into t_permission (id, name, description)
values (1, 'MANAGER_USERS', 'Manager Users');
insert into t_permission (id, name, description)
values (2, 'SEE_BOOKS', 'See Books');
insert into t_permission (id, name, description)
values (3, 'EDIT_BOOKS', 'Edit Books');
insert into t_permission (id, name, description)
values (4, 'CREATE_ORDER', 'Create Order');


insert into t_user_group(id, name)
values (1, 'Administrator');
insert into t_user_group(id, name)
values (2, 'Seller');
insert into t_user_group(id, name)
values (3, 'Buyer');


insert into t_user_group_permission (user_group_id, permission_id)
values (1, 1);
insert into t_user_group_permission (user_group_id, permission_id)
values (1, 2);
insert into t_user_group_permission (user_group_id, permission_id)
values (1, 3);
insert into t_user_group_permission (user_group_id, permission_id)
values (1, 4);
insert into t_user_group_permission (user_group_id, permission_id)
values (2, 2);
insert into t_user_group_permission (user_group_id, permission_id)
values (2, 3);
insert into t_user_group_permission (user_group_id, permission_id)
values (2, 4);
insert into t_user_group_permission (user_group_id, permission_id)
values (3, 2);
insert into t_user_group_permission (user_group_id, permission_id)
values (3, 4);


insert into t_user_user_group(user_id, user_group_id)
values (1, 1);
insert into t_user_user_group(user_id, user_group_id)
values (1, 2);
insert into t_user_user_group(user_id, user_group_id)
values (1, 3);
insert into t_user_user_group(user_id, user_group_id)
values (2, 2);
insert into t_user_user_group(user_id, user_group_id)
values (3, 3);

-- client_id = identificao da aplicao client
-- resource_ids = deve ser null pois o resource-server que é implementado usado a nova spec do spring security não tem suporte, a idea seria identificar o resource-server
-- client_secret = password do client 'web123' gerado atraves do site https://bcrypt-generator.com/
-- scope = .scopes("WRITE", "READ"), importante nota não deve haver espaços entre cada scope
-- authorized_grant_types = // specify what flows we want to use for the current client app,  .authorizedGrantTypes("password", "refresh_token")
-- web_server_redirect_uri = usado para geracao do token, deve ser definido quando o authorized_grant_types é authorization_code
-- authorities = adicionar autorities ao token que é gerado usado no flows (client_credentials) os quais não são o cliente final
-- access_token_validity = Validade do token 6 horas,  6 hours (default is 12 hours) .accessTokenValiditySeconds(60 * 60 * 6)
-- refresh_token_validity = refreshTokenValiditySeconds(12 * 60 * 60) // 12 hours
-- autoapprove = true ou false  se for true aprova os scopes automaticamente sem qualquer validação do resource owner
insert into oauth_client_details (client_id, resource_ids, client_secret,
                                  scope, authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, autoapprove)
values ('food4u-web',
        null,
        '$2y$12$w3igMjsfS5XoAYuowoH3C.54vRFWlcXSHLjX7MwF990Kc2KKKh72e',
        'READ,WRITE',
        'password,refresh_token',
        null,
        null,
        60 * 60 * 6,
        12 * 60 * 60,
        null);



--  // this client is for the access_token introspection
--                     .withClient("food4u-api")
--                     // app-client-secret (client-key)
--                     .secret(passwordEncoder.encode("food4u-api-123"))
insert into oauth_client_details (client_id, resource_ids, client_secret,
                                  scope, authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, autoapprove)
values ('food4u-api',
        null,
        '$2y$12$Eab7u0hwokRZD3HbK7Huv.d3/PRvlcReeG25qvSRVXp9hvej8R2Ae',
        null,
        null,
        null,
        null,
        null,
        null,
        null);


--  // client for the client_credentials flow
--                     .withClient("food4u-batch-app")
--                     .secret(passwordEncoder.encode("food4u-batch-app123"))
--                     .authorizedGrantTypes("client_credentials")
--                     .accessTokenValiditySeconds(6 * 24 * 60 * 60) // 6 days (default is 12 hours)
--                     .scopes("READ")
insert into oauth_client_details (client_id, resource_ids, client_secret,
                                  scope, authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, autoapprove)
values ('food4u-batch-app',
        null,
        '$2y$12$Dr1bnqAB7Y/epppzcarVDe7xE4N3rdq.gm6PqaliSGDn56HeFNQW2',
        'READ',
        'client_credentials',
        null,
        'MANAGER_USERS,SEE_BOOKS,EDIT_BOOKS,CREATE_ORDER',
        6 * 24 * 60 * 60,
        null,
        null);




--    .withClient("food4uAnalytics")
--                     // Client example with authorization_code flow
--                     // 1. generation of authorization-code
--                     //      http://AUTHORIZATION-SERVER:8081/oauth/authorize?
--                     //      response_type=code
--                     //      &client_id=food4u-analytics-client-id
--                     //      &state=ABC
--                     //      &redirect_uri=http//food4u.local
--                     .secret(passwordEncoder.encode("food4uAnalytics"))
--                     .authorizedGrantTypes("authorization_code")
--                     .scopes("WRITE", "READ")
--                     // define the accepted redirect uri for authorization-code generation
--                     .redirectUris("http://food4u.local:8000")


insert into oauth_client_details (client_id, resource_ids, client_secret,
                                  scope, authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, autoapprove)
values ('food4uAnalytics',
        null,
        '$2y$12$xaPG4B.9E3iDIkliMg0NTee/9RMyzUB2E9nGgPyCMeen6RtmiuV5i',
        'READ,WRITE',
        'authorization_code',
        'http://food4u.local:8000',
        null,
        null,
        null,
        null);

--  .withClient("webadmin")
--                     // Authentication Implicit Grant Flow
--                     .authorizedGrantTypes("implicit")
--                     .scopes("WRITE", "READ")
--                     .redirectUris("http://food4u.local:8000") // devia usar outra

insert into oauth_client_details (client_id, resource_ids, client_secret,
                                  scope, authorized_grant_types, web_server_redirect_uri, authorities,
                                  access_token_validity, refresh_token_validity, autoapprove)
values ('webadmin',
        null,
        null,
        'READ,WRITE',
        'implicit',
        'http://food4u.local:8000',
        null,
        null,
        null,
        null);