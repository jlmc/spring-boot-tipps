create sequence if not exists clubs_seq start 1 increment 5;

create table if not exists clubs
(
    id          bigint    not null,
    name        text      not null,
    -- Auditing
    version     smallint  not null,
    created_at  timestamp not null default now(),
    modified_at timestamp not null,
    created_by  text      not null,
    modified_by text      not null,
    constraint pk_clubs primary key (id)
    --constraint uk_configuration_key unique (key)
);

create sequence if not exists teams_seq start 1 increment 5;

create table if not exists teams
(
    id      bigint not null,
    club_id bigint not null references clubs (id),
    season  text   not null,
    constraint pk_teams primary key (id)
);


create table if not exists teams_players
(
    team_id   bigint not null references teams (id),
    player_id bigint not null references players (id),

    constraint uk_teams_players_key unique (team_id, player_id)
);

