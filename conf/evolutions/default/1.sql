# --- !Ups

CREATE TABLE IF NOT EXISTS users (
  user_type   INT,
  username    VARCHAR(30) PRIMARY KEY,
  std_role    ENUM ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') DEFAULT 'RoleUser',
  showHideAgg ENUM ('SHOW', 'HIDE', 'AGGREGATE')               DEFAULT 'SHOW'
);

CREATE TABLE IF NOT EXISTS pw_hashes (
  username VARCHAR(30) PRIMARY KEY,
  pw_hash  VARCHAR(60),

  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS courses (
  id          VARCHAR(30) PRIMARY KEY,
  course_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS users_in_courses (
  username  VARCHAR(30),
  course_id VARCHAR(30),
  role      ENUM ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') DEFAULT 'RoleUser',

  PRIMARY KEY (username, course_id),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (course_id) REFERENCES courses (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Feedback

CREATE TABLE IF NOT EXISTS feedback (
  username  VARCHAR(30),
  tool_url  VARCHAR(30),
  sense     VARCHAR(10),
  used      VARCHAR(10),
  usability VARCHAR(10),
  feedback  VARCHAR(10),
  fairness  VARCHAR(10),
  comment   TEXT,

  PRIMARY KEY (username, tool_url),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Learning paths

CREATE TABLE IF NOT EXISTS learning_paths (
  tool_url VARCHAR(10),
  id       INT,
  title    VARCHAR(50),

  PRIMARY KEY (tool_url, id)
);

CREATE TABLE IF NOT EXISTS learning_path_sections (
  id           INT,
  tool_url     VARCHAR(10),
  path_id      INT,
  section_type VARCHAR(30),
  title        VARCHAR(60),
  content      TEXT,

  PRIMARY KEY (id, tool_url, path_id),
  FOREIGN KEY (tool_url, path_id) REFERENCES learning_paths (tool_url, id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Programming

CREATE TABLE IF NOT EXISTS prog_exercises (
  id                INT,
  semantic_version  VARCHAR(10),
  title             VARCHAR(50),
  author            VARCHAR(50),
  ex_text           TEXT,
  ex_state          ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  folder_identifier VARCHAR(30),
  base              TEXT,
  function_name     VARCHAR(30),
  indent_level      INT,
  output_type       VARCHAR(30),
  base_data_json    TEXT,

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS prog_input_types (
  id          INT,
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  input_name  VARCHAR(20),
  input_type  VARCHAR(20),

  PRIMARY KEY (id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_sample_solutions (
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  language    ENUM ('PYTHON_3', 'JAVA_8') DEFAULT 'PYTHON_3',
  base        TEXT,
  solution    TEXT,

  PRIMARY KEY (exercise_id, ex_sem_ver, language),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_sample_testdata (
  id          INT,
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  input_json  TEXT,
  output      VARCHAR(50),

  PRIMARY KEY (id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_commited_testdata (
  id             INT,
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  input_json     TEXT,
  output         VARCHAR(50),

  username       VARCHAR(50),
  approval_state ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  PRIMARY KEY (id, exercise_id, ex_sem_ver, username),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_uml_cd_parts (
  exercise_id   INT,
  ex_sem_ver    VARCHAR(10),
  class_name    VARCHAR(30),
  class_diagram TEXT,

  PRIMARY KEY (exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_solutions (
  username    VARCHAR(50),
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  part        VARCHAR(30),
  points      DOUBLE,
  max_points  DOUBLE,
  solution    TEXT,
  language    VARCHAR(20),

  PRIMARY KEY (username, exercise_id, ex_sem_ver, part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES prog_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Question

CREATE TABLE IF NOT EXISTS quizzes (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  theme            VARCHAR(50),
  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS questions (
  id               INT,
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',
  semantic_version VARCHAR(10),

  collection_id    INT,
  coll_sem_ver     VARCHAR(10),
  question_type    ENUM ('CHOICE', 'FREETEXT', 'FILLOUT')               DEFAULT 'CHOICE',
  max_points       INT,

  PRIMARY KEY (id, semantic_version, collection_id, coll_sem_ver),
  FOREIGN KEY (collection_id, coll_sem_ver) REFERENCES quizzes (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_answers (
  id            INT,
  question_id   INT,
  ex_sem_ver    VARCHAR(10),
  collection_id INT,
  coll_sem_ver  VARCHAR(10),
  answer_text   TEXT,
  correctness   ENUM ('CORRECT', 'OPTIONAL', 'WRONG') DEFAULT 'WRONG',
  explanation   TEXT,

  PRIMARY KEY (id, question_id, ex_sem_ver, collection_id, coll_sem_ver),
  FOREIGN KEY (question_id, ex_sem_ver, collection_id, coll_sem_ver) REFERENCES questions (id, semantic_version, collection_id, coll_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS question_solutions (
  username      VARCHAR(50),
  exercise_id   INT,
  ex_sem_ver    VARCHAR(10),
  collection_id INT,
  coll_sem_ver  VARCHAR(10),
  points        INT,
  max_points    INT,
  answers       TEXT,

  PRIMARY KEY (username, collection_id, coll_sem_ver, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) REFERENCES questions (id, semantic_version, collection_id, coll_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Rose

CREATE TABLE IF NOT EXISTS rose_exercises (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  field_width      INT,
  field_height     INT,
  is_mp            BOOLEAN,

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS rose_inputs (
  id          INT,
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  input_name  VARCHAR(20),
  input_type  VARCHAR(20),

  PRIMARY KEY (id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES rose_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rose_samples (
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  language    ENUM ('PYTHON_3', 'JAVA_8') DEFAULT 'PYTHON_3',
  solution    TEXT,

  PRIMARY KEY (exercise_id, ex_sem_ver, language),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES rose_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rose_solutions (
  username    VARCHAR(50),
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  part        VARCHAR(30),
  points      DOUBLE,
  max_points  DOUBLE,
  solution    TEXT,

  PRIMARY KEY (username, exercise_id, ex_sem_ver, part),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES rose_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rose_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES rose_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Spread

CREATE TABLE IF NOT EXISTS spread_exercises (
  id                INT,
  semantic_version  VARCHAR(10),
  title             VARCHAR(50),
  author            VARCHAR(50),
  ex_text           TEXT,
  ex_state          ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  sample_filename   VARCHAR(50),
  template_filename VARCHAR(50),

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS spread_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES spread_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Sql

CREATE TABLE IF NOT EXISTS sql_scenarioes (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  shortName        VARCHAR(50),
  scriptFile       VARCHAR(50),

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS sql_exercises (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED')    DEFAULT 'RESERVED',

  collection_id    INT,
  coll_sem_ver     VARCHAR(10),
  tags             TEXT,
  exercise_type    ENUM ('SELECT', 'CREATE', 'UPDATE', 'INSERT', 'DELETE') DEFAULT 'SELECT',
  hint             TEXT,

  PRIMARY KEY (id, semantic_version, collection_id, coll_sem_ver),
  FOREIGN KEY (collection_id, coll_sem_ver) REFERENCES sql_scenarioes (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sql_samples (
  id            INT,
  exercise_id   INT,
  ex_sem_ver    VARCHAR(10),
  collection_id INT,
  coll_sem_ver  VARCHAR(10),
  sample        TEXT,

  PRIMARY KEY (id, exercise_id, ex_sem_ver, collection_id, coll_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) REFERENCES sql_exercises (id, semantic_version, collection_id, coll_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sql_solutions (
  username      VARCHAR(50),
  exercise_id   INT,
  ex_sem_ver    VARCHAR(10),
  collection_id INT,
  coll_sem_ver  VARCHAR(10),
  points        DOUBLE,
  max_points    DOUBLE,
  solution      TEXT,

  PRIMARY KEY (username, exercise_id, ex_sem_ver, collection_id, coll_sem_ver),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (exercise_id, ex_sem_ver, collection_id, coll_sem_ver) REFERENCES sql_exercises (id, semantic_version, collection_id, coll_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Uml

CREATE TABLE IF NOT EXISTS uml_exercises (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  solution_json    TEXT,
  marked_text      TEXT,
  to_ignore        TEXT,

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS uml_mappings (
  exercise_id   INT,
  ex_sem_ver    VARCHAR(10),
  mapping_key   VARCHAR(50),
  mapping_value VARCHAR(50),

  PRIMARY KEY (exercise_id, ex_sem_ver, mapping_key),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES uml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_solutions (
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  username    VARCHAR(30),
  part        VARCHAR(30),
  points      DOUBLE,
  max_points  DOUBLE,
  solution    TEXT,

  PRIMARY KEY (username, exercise_id, ex_sem_ver, part),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES uml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES uml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Web

CREATE TABLE IF NOT EXISTS web_exercises (
  id               INT,
  semantic_version VARCHAR(10),
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  html_text        TEXT,
  js_text          TEXT,
  php_text         TEXT,

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS html_tasks (
  task_id      INT,
  exercise_id  INT,
  ex_sem_ver   VARCHAR(10),
  text         TEXT,
  xpath_query  VARCHAR(50),

  text_content VARCHAR(100),

  PRIMARY KEY (task_id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES web_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS html_attributes (
  attr_key    VARCHAR(30),
  attr_value  VARCHAR(150),
  task_id     INT,
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),

  PRIMARY KEY (attr_key, task_id, exercise_id, ex_sem_ver),
  FOREIGN KEY (task_id, exercise_id, ex_sem_ver) REFERENCES html_tasks (task_id, exercise_id, ex_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS js_tasks (
  task_id            INT,
  exercise_id        INT,
  ex_sem_ver         VARCHAR(10),
  text               TEXT,
  xpath_query        VARCHAR(50),

  action_type        ENUM ('CLICK', 'FILLOUT') DEFAULT 'CLICK',
  action_xpath_query VARCHAR(50),
  keys_to_send       VARCHAR(100),

  PRIMARY KEY (task_id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES web_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS js_conditions (
  condition_id    INT,
  task_id         INT,
  exercise_id     INT,
  ex_sem_ver      VARCHAR(10),

  xpath_query     VARCHAR(50),
  is_precondition BOOLEAN DEFAULT TRUE,
  awaited_value   VARCHAR(50),

  PRIMARY KEY (condition_id, task_id, exercise_id, ex_sem_ver),
  FOREIGN KEY (task_id, exercise_id, ex_sem_ver) REFERENCES js_tasks (task_id, exercise_id, ex_sem_ver)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS web_solutions (
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  username    VARCHAR(30),
  part        VARCHAR(30),
  points      DOUBLE,
  max_points  DOUBLE,
  solution    TEXT,

  PRIMARY KEY (exercise_id, ex_sem_ver, username, part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES web_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS web_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES web_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Xml

CREATE TABLE IF NOT EXISTS xml_exercises (
  id                  INT,
  semantic_version    VARCHAR(10),
  title               VARCHAR(50),
  author              VARCHAR(50),
  ex_text             TEXT,
  ex_state            ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  grammar_description TEXT,
  root_node           VARCHAR(30),

  PRIMARY KEY (id, semantic_version)
);

CREATE TABLE IF NOT EXISTS xml_sample_grammars (
  id             INT,
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  sample_grammar TEXT,

  PRIMARY KEY (id, exercise_id, ex_sem_ver),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES xml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS xml_solutions (
  exercise_id INT,
  ex_sem_ver  VARCHAR(10),
  username    VARCHAR(50),
  part        VARCHAR(30),
  points      DOUBLE,
  max_points  DOUBLE,
  solution    TEXT,

  PRIMARY KEY (exercise_id, ex_sem_ver, username, part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES xml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS xml_exercise_reviews (
  username VARCHAR(50),
  exercise_id    INT,
  ex_sem_ver     VARCHAR(10),
  exercise_part  VARCHAR(30),
  difficulty     VARCHAR(20),
  maybe_duration INT,

  PRIMARY KEY (exercise_id, ex_sem_ver, exercise_part),
  FOREIGN KEY (exercise_id, ex_sem_ver) REFERENCES xml_exercises (id, semantic_version)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# --- !Downs

# Xml

DROP TABLE IF EXISTS xml_exercise_reviews;

DROP TABLE IF EXISTS xml_solutions;

DROP TABLE IF EXISTS xml_sample_grammars;

DROP TABLE IF EXISTS xml_exercises;

# Web

DROP TABLE IF EXISTS web_exercise_reviews;

DROP TABLE IF EXISTS web_solutions;

DROP TABLE IF EXISTS js_conditions;

DROP TABLE IF EXISTS js_tasks;

DROP TABLE IF EXISTS html_attributes;

DROP TABLE IF EXISTS html_tasks;

DROP TABLE IF EXISTS web_exercises;

# Uml

DROP TABLE IF EXISTS uml_exercise_reviews;

DROP TABLE IF EXISTS uml_solutions;

DROP TABLE IF EXISTS uml_mappings;

DROP TABLE IF EXISTS uml_exercises;

# Spread

DROP TABLE IF EXISTS spread_exercise_reviews;

DROP TABLE IF EXISTS spread_exercises;

# Sql

DROP TABLE IF EXISTS sql_solutions;

DROP TABLE IF EXISTS sql_samples;

DROP TABLE IF EXISTS sql_exercises;

DROP TABLE IF EXISTS sql_scenarioes;

# Rose

DROP TABLE IF EXISTS rose_exercise_reviews;

DROP TABLE IF EXISTS rose_solutions;

DROP TABLE IF EXISTS rose_samples;

DROP TABLE IF EXISTS rose_inputs;

DROP TABLE IF EXISTS rose_exercises;

# Questions

DROP TABLE IF EXISTS question_solutions;

DROP TABLE IF EXISTS question_answers;

DROP TABLE IF EXISTS questions;

DROP TABLE IF EXISTS quizzes;

# Programming

DROP TABLE IF EXISTS prog_exercise_reviews;

DROP TABLE IF EXISTS prog_solutions;

DROP TABLE IF EXISTS prog_uml_cd_parts;

DROP TABLE IF EXISTS prog_commited_testdata;

DROP TABLE IF EXISTS prog_sample_testdata;

DROP TABLE IF EXISTS prog_sample_solutions;

DROP TABLE IF EXISTS prog_input_types;

DROP TABLE IF EXISTS prog_exercises;

# General

DROP TABLE IF EXISTS learning_path_sections;

DROP TABLE IF EXISTS learning_paths;

DROP TABLE IF EXISTS feedback;

DROP TABLE IF EXISTS users_in_courses;

DROP TABLE IF EXISTS courses;

DROP TABLE IF EXISTS pw_hashes;

DROP TABLE IF EXISTS users;

