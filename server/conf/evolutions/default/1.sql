# --- !Ups

create table if not exists users (
    user_type int,
    username  varchar(30) primary key,
    std_role  enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser'
);

create table if not exists pw_hashes (
    username varchar(30) primary key,
    pw_hash  varchar(60),

    foreign key (username)
        references users (username)
        on update cascade
        on delete cascade
);

create table if not exists courses (
    id          varchar(30) primary key,
    course_name varchar(100)
);

create table if not exists users_in_courses (
    username  varchar(30),
    course_id varchar(30),
    role      enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser',

    primary key (username, course_id),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (course_id) references courses (id)
        on update cascade on delete cascade
);

# Feedback

create table if not exists feedback (
    username          varchar(30),
    tool_url          varchar(30),
    marks_json        longtext not null, # TODO: json
    # -- TODO: remove following columns?!
    sense             enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    used              enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    usability         enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    style_feedback    enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    fairness_feedback enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    comment           text,

    primary key (username, tool_url),
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

# Learning paths

create table if not exists learning_paths (
    tool_url varchar(10),
    id       int,
    title    varchar(50),

    primary key (tool_url, id)
);

create table if not exists learning_path_sections (
    id           int,
    tool_url     varchar(10),
    path_id      int,
    section_type varchar(30),
    title        varchar(60),
    content      text,

    primary key (id, tool_url, path_id),
    foreign key (tool_url, path_id) references learning_paths (tool_url, id)
        on update cascade on delete cascade
);

# Collections and Exercises

create table if not exists collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50)  not null,
    authors    longtext     not null, # TODO: json!
    ex_text    text         not null,
    state_json varchar(100) not null,
    short_name varchar(50)  not null,

    primary key (id, tool_id)
);

create table if not exists exercises (
    id               int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(100),

    title            varchar(50)  not null,
    authors          longtext     not null, # TODO: json!
    ex_text          text         not null,
    state_json       varchar(100) not null,
    tags             longtext     not null, # TODO: json!

    content_json     longtext     not null, # TODO: json!

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists user_solutions (
    id                        int,
    exercise_id               int,
    collection_id             int,
    tool_id                   varchar(20),
    exercise_semantic_version varchar(100),
    part                      varchar(50),
    username                  varchar(50),

    solution_json             longtext not null, # TODO: json!
    points                    double,
    max_points                double,

    primary key (id, exercise_id, collection_id, tool_id, exercise_semantic_version, part, username),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, exercise_semantic_version)
        references exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists exercise_reviews (
    username                  varchar(50),
    exercise_id               int,
    collection_id             int,
    tool_id                   varchar(20),
    exercise_semantic_version varchar(10),
    part                      varchar(50),

    difficulty                enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration            int,

    primary key (username, exercise_id, collection_id, tool_id, exercise_semantic_version, part),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, exercise_semantic_version)
        references exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# --- !Downs

drop table if exists exercise_reviews;

drop table if exists user_solutions;

drop table if exists exercises;

drop table if exists collections;


drop table if exists learning_path_sections;

drop table if exists learning_paths;


drop table if exists feedback;


drop table if exists users_in_courses;

drop table if exists courses;

drop table if exists pw_hashes;

drop table if exists users;
