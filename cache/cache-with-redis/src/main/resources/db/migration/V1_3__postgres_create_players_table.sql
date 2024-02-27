create sequence if not exists players_seq start 1 increment 5;

create table if not exists players
(
    id          bigint              not null,
    name        text                not null,
    birthdate   date,
    country_code varchar(50)        not null,
    -- Auditing
    version     bigint              not null,
    created_at  timestamp        not null default now(),
    modified_at timestamp         not null,
    created_by  text                not null,
    modified_by text                not null,

    constraint pk_players primary key (id)
    --constraint uk_configuration_key unique (key)
    );
