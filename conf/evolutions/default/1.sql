# --- !Ups

create table if not exists users (
    username varchar(30)                                      not null primary key,
    pw_hash  varchar(60),
    std_role enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') not null default 'RoleUser'
);

# Lessons

create table if not exists lessons (
    id           int,
    tool_id      varchar(20),
    title        varchar(100) not null,
    description  varchar(300) not null,
    content_json json         not null,

    primary key (id, tool_id)
);

# Proficiencies

create table if not exists topics (
    id           int,
    tool_id      varchar(10),
    abbreviation varchar(10)  not null,
    title        varchar(100) not null,

    primary key (id, tool_id)
);

create table if not exists tool_proficiencies (
    username varchar(30) references users (username) on update cascade on delete cascade,
    tool_id  varchar(10),
    points   int not null,

    primary key (username, tool_id)
);

create table if not exists topic_proficiencies (
    username varchar(30) references users (username) on update cascade on delete cascade,
    topic_id int,
    tool_id  varchar(10),
    points   int not null,

    primary key (username, topic_id, tool_id),
    foreign key (topic_id, tool_id) references topics (id, tool_id) on update cascade on delete cascade
);

# Collections and Exercises

create table if not exists collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50) not null,
    authors    json        not null,
    ex_text    text        not null,
    short_name varchar(50) not null,

    primary key (id, tool_id)
);

create table if not exists exercises (
    id                    int,
    collection_id         int,
    tool_id               varchar(20),
    title                 varchar(50) not null,
    authors               json        not null,
    ex_text               text        not null,
    difficulty            int         not null,
    sample_solutions_json json        not null,
    content_json          json        not null,

    primary key (id, collection_id, tool_id),
    foreign key (collection_id, tool_id) references collections (id, tool_id) on update cascade on delete cascade
);

create table if not exists exercise_topics (
    topic_id      int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),

    primary key (topic_id, exercise_id, collection_id, tool_id),
    foreign key (exercise_id, collection_id, tool_id) references exercises (id, collection_id, tool_id) on update cascade on delete cascade,
    foreign key (topic_id, tool_id) references topics (id, tool_id) on update cascade on delete cascade
);

create table if not exists user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    part          varchar(50) not null,
    username      varchar(50) references users (username) on update cascade on delete cascade,

    solution_json json        not null,
    points        double      not null,
    max_points    double      not null,

    primary key (id, exercise_id, collection_id, tool_id, part, username),
    foreign key (exercise_id, collection_id, tool_id) references exercises (id, collection_id, tool_id, semantic_version) on update cascade on delete cascade
);


# --- !Downs


drop table if exists user_solutions;

drop table if exists exercise_topics;

drop table if exists exercises;

drop table if exists collections;


drop table if exists topic_proficiencies;

drop table if exists tool_proficiencies;

drop table if exists topics;


drop table if exists lessons;


drop table if exists users;
