-- !Ups

create table if not exists users (
  username      varchar(100) not null primary key,
  maybe_pw_hash varchar(100)
);

create table if not exists topics (
  tool_id      varchar(20)  not null,
  abbreviation varchar(20)  not null,
  title        varchar(100) not null,

  primary key (tool_id, abbreviation),
  unique (tool_id, abbreviation)
);

create table if not exists collections (
  tool_id       varchar(20)  not null,
  collection_id int          not null,
  title         varchar(100) not null,

  primary key (tool_id, collection_id),

  unique (tool_id, title)
);

create table if not exists exercises (
  tool_id       varchar(20)                                             not null,
  collection_id int                                                     not null,
  exercise_id   int                                                     not null,
  title         varchar(100)                                            not null,
  text          text                                                    not null,
  difficulty    enum ('Beginner', 'Intermediate', 'Advanced', 'Expert') not null,
  content_json  json                                                    not null,

  primary key (tool_id, collection_id, exercise_id),

  foreign key (tool_id, collection_id)
    references collections (tool_id, collection_id)
    on update cascade on delete cascade
);

create table if not exists exercise_topics (
  tool_id            varchar(20)                                             not null,
  collection_id      int                                                     not null,
  exercise_id        int                                                     not null,
  topic_abbreviation varchar(20)                                             not null,
  level              enum ('Beginner', 'Intermediate', 'Advanced', 'Expert') not null,

  primary key (tool_id, collection_id, exercise_id, topic_abbreviation),

  foreign key (tool_id, collection_id, exercise_id)
    references exercises (tool_id, collection_id, exercise_id)
    on update cascade on delete cascade,
  foreign key (tool_id, topic_abbreviation)
    references topics (tool_id, abbreviation)
    on update cascade on delete cascade
);

create table if not exists user_solutions_without_parts (
  tool_id             varchar(20)  not null,
  collection_id       int          not null,
  exercise_id         int          not null,
  username            varchar(100) not null,
  solution_id         int          not null,

  solution_json       json         not null,
  points_quarters     int          not null,
  max_points_quarters int          not null,

  primary key (tool_id, collection_id, exercise_id, username, solution_id),

  foreign key (tool_id, collection_id, exercise_id)
    references exercises (tool_id, collection_id, exercise_id)
    on update cascade on delete cascade,
  foreign key (username)
    references users (username)
    on update cascade on delete cascade
);

create table if not exists user_solutions_with_parts (
  tool_id             varchar(20)  not null,
  collection_id       int          not null,
  exercise_id         int          not null,
  username            varchar(100) not null,
  part_id             varchar(20)  not null,
  solution_id         int          not null,

  solution_json       json         not null,
  points_quarters     int          not null,
  max_points_quarters int          not null,

  primary key (tool_id, collection_id, exercise_id, username, part_id, solution_id),

  foreign key (tool_id, collection_id, exercise_id)
    references exercises (tool_id, collection_id, exercise_id)
    on update cascade on delete cascade,
  foreign key (username)
    references users (username)
    on update cascade on delete cascade
);

-- !Downs

drop table if exists
  user_solutions_with_parts,
  user_solutions_without_parts,
  exercise_topics,
  exercises,
  collections,
  users;

