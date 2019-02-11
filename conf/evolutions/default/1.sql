# --- !Ups

create table if not exists users
(
  user_type   int,
  username    varchar(30) primary key,
  std_role    enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser',
  showHideAgg enum ('SHOW', 'HIDE', 'AGGREGATE')               default 'SHOW'
);

create table if not exists pw_hashes
(
  username varchar(30) primary key,
  pw_hash  varchar(60),

  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

create table if not exists courses
(
  id          varchar(30) primary key,
  course_name varchar(100)
);

create table if not exists users_in_courses
(
  username  varchar(30),
  course_id varchar(30),
  role      enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser',

  primary key (username, course_id),
  foreign key (username) references users (username)
    on update cascade
    on delete cascade,
  foreign key (course_id) references courses (id)
    on update cascade
    on delete cascade
);

# Feedback

create table if not exists feedback
(
  username          varchar(30),
  tool_url          varchar(30),
  sense             enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
  used              enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
  usability         enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
  style_feedback    enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
  fairness_feedback enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
  comment           text,

  primary key (username, tool_url),
  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

# Learning paths

create table if not exists learning_paths
(
  tool_url varchar(10),
  id       int,
  title    varchar(50),

  primary key (tool_url, id)
);

create table if not exists learning_path_sections
(
  id           int,
  tool_url     varchar(10),
  path_id      int,
  section_type varchar(30),
  title        varchar(60),
  content      text,

  primary key (id, tool_url, path_id),
  foreign key (tool_url, path_id) references learning_paths (tool_url, id)
    on update cascade
    on delete cascade
);

# Programming

create table if not exists prog_exercises
(
  id                int,
  semantic_version  varchar(10),
  title             varchar(50),
  author            varchar(50),
  ex_text           text,
  ex_state          enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  folder_identifier varchar(30),
  function_name     varchar(30),
  indent_level      int,
  output_type       varchar(30),
  base_data_json    text,

  primary key (id, semantic_version)
);

create table if not exists prog_input_types
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),
  input_name  varchar(20),
  input_type  varchar(20),

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists prog_sample_solutions
(
  exercise_id int,
  ex_sem_ver  varchar(10),
  language    enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',
  base        text,
  solution    text,

  primary key (exercise_id, ex_sem_ver, language),
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists prog_sample_testdata
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),
  input_json  text,
  output      varchar(50),

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists prog_commited_testdata
(
  id             int,
  exercise_id    int,
  ex_sem_ver     varchar(10),
  input_json     text,
  output         varchar(50),

  username       varchar(50),
  approval_state enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  primary key (id, exercise_id, ex_sem_ver, username),
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade,
  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

create table if not exists prog_uml_cd_parts
(
  exercise_id   int,
  ex_sem_ver    varchar(10),
  class_name    varchar(30),
  class_diagram text,

  primary key (exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists prog_solutions
(
  id                  int primary key auto_increment,
  username            varchar(50),
  exercise_id         int,
  ex_sem_ver          varchar(10),
  part                varchar(30),
  points              double,
  max_points          double,
  solution            text,
  language            varchar(20),
  extended_unit_tests boolean default false,

  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade,
  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

create table if not exists prog_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (username) references users (username)
    on update cascade
    on delete cascade,
  foreign key (exercise_id, ex_sem_ver) references prog_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# Question

create table if not exists quizzes
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  theme            varchar(50),

  primary key (id, semantic_version)
);

create table if not exists questions
(
  id               int,
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
  semantic_version varchar(10),

  collection_id    int,
  coll_sem_ver     varchar(10),
  question_type    enum ('CHOICE', 'FREETEXT', 'FILLOUT')               default 'CHOICE',
  max_points       int,

  primary key (id, semantic_version, collection_id, coll_sem_ver),
  foreign key (collection_id, coll_sem_ver) references quizzes (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists question_answers
(
  id            int,
  question_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  coll_sem_ver  varchar(10),
  answer_text   text,
  correctness   enum ('CORRECT', 'OPTIONAL', 'WRONG') default 'WRONG',
  explanation   text,

  primary key (id, question_id, ex_sem_ver, collection_id, coll_sem_ver),
  foreign key (question_id, ex_sem_ver, collection_id, coll_sem_ver) references questions (id, semantic_version, collection_id, coll_sem_ver)
    on update cascade
    on delete cascade
);

create table if not exists question_solutions
(
  id            int primary key auto_increment,
  username      varchar(50),
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  coll_sem_ver  varchar(10),
  points        int,
  max_points    int,
  answers       text,

  foreign key (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) references questions (id, semantic_version, collection_id, coll_sem_ver)
    on update cascade
    on delete cascade
);

# Rose

create table if not exists rose_exercises
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  field_width      int,
  field_height     int,
  is_mp            boolean,

  primary key (id, semantic_version)
);

create table if not exists rose_inputs
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),
  input_name  varchar(20),
  input_type  varchar(20),

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references rose_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists rose_samples
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),
  language    enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',
  solution    text,

  primary key (id, exercise_id, ex_sem_ver, language),
  foreign key (exercise_id, ex_sem_ver) references rose_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists rose_solutions
(
  id          int primary key auto_increment,
  username    varchar(50),
  exercise_id int,
  ex_sem_ver  varchar(10),
  part        varchar(30),
  points      double,
  max_points  double,
  solution    text,

  foreign key (username) references users (username)
    on update cascade
    on delete cascade,
  foreign key (exercise_id, ex_sem_ver) references rose_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists rose_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (exercise_id, ex_sem_ver) references rose_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# Spread

create table if not exists spread_exercises
(
  id                int,
  semantic_version  varchar(10),
  title             varchar(50),
  author            varchar(50),
  ex_text           text,
  ex_state          enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  sample_filename   varchar(50),
  template_filename varchar(50),

  primary key (id, semantic_version)
);

create table if not exists spread_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (exercise_id, ex_sem_ver) references spread_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# Sql

create table if not exists sql_scenarioes
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  shortName        varchar(50),
  scriptFile       varchar(50),

  primary key (id, semantic_version)
);

create table if not exists sql_exercises
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED')    default 'RESERVED',

  collection_id    int,
  coll_sem_ver     varchar(10),
  tags             text,
  exercise_type    enum ('SELECT', 'CREATE', 'UPDATE', 'INSERT', 'DELETE') default 'SELECT',
  hint             text,

  primary key (id, semantic_version, collection_id, coll_sem_ver),
  foreign key (collection_id, coll_sem_ver) references sql_scenarioes (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists sql_samples
(
  id            int,
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  coll_sem_ver  varchar(10),
  sample        text,

  primary key (id, exercise_id, ex_sem_ver, collection_id, coll_sem_ver),
  foreign key (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) references sql_exercises (id, semantic_version, collection_id, coll_sem_ver)
    on update cascade
    on delete cascade
);

create table if not exists sql_solutions
(
  id            int primary key auto_increment,
  username      varchar(50),
  exercise_id   int,
  ex_sem_ver    varchar(10),
  collection_id int,
  coll_sem_ver  varchar(10),
  points        double,
  max_points    double,
  solution      text,

  foreign key (username) references users (username)
    on update cascade
    on delete cascade,
  foreign key (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) references sql_exercises (id, semantic_version, collection_id, coll_sem_ver)
    on update cascade
    on delete cascade
);

# Uml

create table if not exists uml_exercises
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  marked_text      text,

  primary key (id, semantic_version)
);

create table if not exists uml_to_ignore
(
  exercise_id int,
  ex_sem_ver  varchar(10),
  to_ignore   varchar(50),

  primary key (exercise_id, ex_sem_ver, to_ignore),
  foreign key (exercise_id, ex_sem_ver) references uml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists uml_mappings
(
  exercise_id   int,
  ex_sem_ver    varchar(10),
  mapping_key   varchar(50),
  mapping_value varchar(50),

  primary key (exercise_id, ex_sem_ver, mapping_key),
  foreign key (exercise_id, ex_sem_ver) references uml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists uml_sample_solutions
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),
  sample      text,

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references uml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists uml_solutions
(
  id          int primary key auto_increment,
  exercise_id int,
  ex_sem_ver  varchar(10),
  username    varchar(30),
  part        varchar(30),
  points      double,
  max_points  double,
  solution    text,

  foreign key (username) references users (username)
    on update cascade
    on delete cascade,
  foreign key (exercise_id, ex_sem_ver) references uml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists uml_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (exercise_id, ex_sem_ver) references uml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# Web

create table if not exists web_exercises
(
  id               int,
  semantic_version varchar(10),
  title            varchar(50),
  author           varchar(50),
  ex_text          text,
  ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  html_text        text,
  js_text          text,

  primary key (id, semantic_version)
);

create table if not exists html_tasks
(
  task_id      int,
  exercise_id  int,
  ex_sem_ver   varchar(10),
  text         text,
  xpath_query  varchar(50),

  text_content varchar(100),

  primary key (task_id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references web_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists html_attributes
(
  attr_key    varchar(30),
  attr_value  varchar(150),
  task_id     int,
  exercise_id int,
  ex_sem_ver  varchar(10),

  primary key (attr_key, task_id, exercise_id, ex_sem_ver),
  foreign key (task_id, exercise_id, ex_sem_ver) references html_tasks (task_id, exercise_id, ex_sem_ver)
    on update cascade
    on delete cascade
);

create table if not exists js_tasks
(
  task_id            int,
  exercise_id        int,
  ex_sem_ver         varchar(10),
  text               text,
  xpath_query        varchar(50),

  action_type        enum ('CLICK', 'FILLOUT') default 'CLICK',
  action_xpath_query varchar(50),
  keys_to_send       varchar(100),

  primary key (task_id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references web_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists js_conditions
(
  condition_id    int,
  task_id         int,
  exercise_id     int,
  ex_sem_ver      varchar(10),

  xpath_query     varchar(50),
  is_precondition boolean default true,
  awaited_value   varchar(50),

  primary key (condition_id, task_id, exercise_id, ex_sem_ver),
  foreign key (task_id, exercise_id, ex_sem_ver) references js_tasks (task_id, exercise_id, ex_sem_ver)
    on update cascade
    on delete cascade
);

create table if not exists web_sample_solutions
(
  id          int,
  exercise_id int,
  ex_sem_ver  varchar(10),

  html_sample text,
  js_sample   text,

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references web_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists web_solutions
(
  id          int primary key auto_increment,
  exercise_id int,
  ex_sem_ver  varchar(10),
  username    varchar(30),
  part        varchar(30),
  points      double,
  max_points  double,
  solution    text,

  foreign key (exercise_id, ex_sem_ver) references web_exercises (id, semantic_version)
    on update cascade
    on delete cascade,
  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

create table if not exists web_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (exercise_id, ex_sem_ver) references web_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# Xml

create table if not exists xml_exercises
(
  id                  int,
  semantic_version    varchar(10),
  title               varchar(50),
  author              varchar(50),
  ex_text             text,
  ex_state            enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

  grammar_description text,
  root_node           varchar(30),

  primary key (id, semantic_version)
);

create table if not exists xml_samples
(
  id              int,
  exercise_id     int,
  ex_sem_ver      varchar(10),
  sample_grammar  text,
  sample_document text,

  primary key (id, exercise_id, ex_sem_ver),
  foreign key (exercise_id, ex_sem_ver) references xml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

create table if not exists xml_solutions
(
  id          int primary key auto_increment,
  exercise_id int,
  ex_sem_ver  varchar(10),
  username    varchar(50),
  part        varchar(30),
  points      double,
  max_points  double,
  solution    text,

  foreign key (exercise_id, ex_sem_ver) references xml_exercises (id, semantic_version)
    on update cascade
    on delete cascade,
  foreign key (username) references users (username)
    on update cascade
    on delete cascade
);

create table if not exists xml_exercise_reviews
(
  username       varchar(50),
  exercise_id    int,
  ex_sem_ver     varchar(10),
  exercise_part  varchar(30),
  difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
  maybe_duration int,

  primary key (username, exercise_id, ex_sem_ver, exercise_part),
  foreign key (exercise_id, ex_sem_ver) references xml_exercises (id, semantic_version)
    on update cascade
    on delete cascade
);

# --- !Downs

# Xml

drop table if exists xml_exercise_reviews;

drop table if exists xml_solutions;

drop table if exists xml_samples;

drop table if exists xml_exercises;

# Web

drop table if exists web_exercise_reviews;

drop table if exists web_solutions;

drop table if exists web_sample_solutions;

drop table if exists js_conditions;

drop table if exists js_tasks;

drop table if exists html_attributes;

drop table if exists html_tasks;

drop table if exists web_exercises;

# Uml

drop table if exists uml_exercise_reviews;

drop table if exists uml_solutions;

drop table if exists uml_sample_solutions;

drop table if exists uml_mappings;

drop table if exists uml_to_ignore;

drop table if exists uml_exercises;

# Spread

drop table if exists spread_exercise_reviews;

drop table if exists spread_exercises;

# Sql

drop table if exists sql_solutions;

drop table if exists sql_samples;

drop table if exists sql_exercises;

drop table if exists sql_scenarioes;

# Rose

drop table if exists rose_exercise_reviews;

drop table if exists rose_solutions;

drop table if exists rose_samples;

drop table if exists rose_inputs;

drop table if exists rose_exercises;

# Questions

drop table if exists question_solutions;

drop table if exists question_answers;

drop table if exists questions;

drop table if exists quizzes;

# Programming

drop table if exists prog_exercise_reviews;

drop table if exists prog_solutions;

drop table if exists prog_uml_cd_parts;

drop table if exists prog_commited_testdata;

drop table if exists prog_sample_testdata;

drop table if exists prog_sample_solutions;

drop table if exists prog_input_types;

drop table if exists prog_exercises;

# General

drop table if exists learning_path_sections;

drop table if exists learning_paths;

drop table if exists feedback;

drop table if exists users_in_courses;

drop table if exists courses;

drop table if exists pw_hashes;

drop table if exists users;

