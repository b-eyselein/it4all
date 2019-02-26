# --- !Ups

create table if not exists regex_collections
(
  id         int primary key,
  title      varchar(50),
  author     varchar(50),
  ex_text    text,
  ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
  short_name varchar(50)
);

create table if not exists regex_exercises
(
  id               int,
  semantic_version varchar(10),
  collection_id    int,
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  primary key (id, semantic_version, collection_id),
  foreign key (collection_id) references regex_collections (id)
    on update cascade on delete cascade
);

create table if not exists regex_sample_solutions
(
  id            int,
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,

  sample        varchar(100),
  primary key (id, exercise_id, ex_sem_ver, collection_id),
  foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
    on update cascade on delete cascade
);

create table if not exists regex_test_data
(
  id            int,
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  data          varchar(100),
  is_included   boolean,

  primary key (id, exercise_id, ex_sem_ver, collection_id),
  foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
    on update cascade on delete cascade
);

create table if not exists regex_solutions
(
  id            int primary key auto_increment,
  username      varchar(50),
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  part          varchar(30),
  points        double,
  max_points    double,
  solution      varchar(100),

  foreign key (username) references users (username)
    on update cascade on delete cascade,
  foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
    on update cascade on delete cascade
);

create table if not exists regex_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  collection_id  int,
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, collection_id, exercise_part),
  foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
    on update cascade on delete cascade
);

# --- !Downs

drop table if exists regex_exercise_reviews;

drop table if exists regex_solutions;

drop table if exists regex_test_data;

drop table if exists regex_sample_solutions;

drop table if exists regex_exercises;

drop table if exists regex_collections;
