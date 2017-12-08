# --- !Ups

CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(30) PRIMARY KEY,
  pw_hash  VARCHAR(60),
  std_role ENUM ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') DEFAULT 'RoleUser',
  todo     ENUM ('SHOW', 'HIDE', 'AGGR')                    DEFAULT 'SHOW'
);

CREATE TABLE IF NOT EXISTS courses (
  id          INT PRIMARY KEY  AUTO_INCREMENT,
  course_name VARCHAR(50)
);

# Ebnf

CREATE TABLE IF NOT EXISTS ebnf_exercises (
  id        INT PRIMARY KEY,
  title     VARCHAR(50),
  author    VARCHAR(50),
  ex_text   TEXT,
  ex_state  ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  terminals VARCHAR(30)
);

# Programming

CREATE TABLE IF NOT EXISTS prog_exercises (
  id            INT PRIMARY KEY,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  function_name VARCHAR(30),
  input_count   INT
);

CREATE TABLE IF NOT EXISTS prog_samples (
  exercise_id INT,
  language    ENUM ('PYTHON_3', 'JAVA_8') DEFAULT 'PYTHON_3',
  solution    TEXT,

  PRIMARY KEY (exercise_id, language),
  FOREIGN KEY (exercise_id) REFERENCES prog_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_sample_testdata (
  id          INT,
  exercise_id INT,
  output      VARCHAR(50),

  PRIMARY KEY (id, exercise_id),
  FOREIGN KEY (exercise_id) REFERENCES prog_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_commited_testdata (
  id          INT,
  exercise_id INT,
  input       VARCHAR(50),
  username    VARCHAR(50),
  state       ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  PRIMARY KEY (id, exercise_id, username),
  FOREIGN KEY (exercise_id) REFERENCES prog_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_sample_testdata_input (
  id          INT,
  test_id     INT,
  exercise_id INT,
  input       VARCHAR(50),

  PRIMARY KEY (id, test_id, exercise_id),
  FOREIGN KEY (test_id, exercise_id) REFERENCES prog_sample_testdata (id, exercise_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_commited_testdata_input (
  id          INT,
  test_id     INT,
  exercise_id INT,
  input       VARCHAR(50),
  username    VARCHAR(50),

  PRIMARY KEY (username, id, test_id, exercise_id),
  FOREIGN KEY (test_id, exercise_id, username) REFERENCES prog_commited_testdata (id, exercise_id, username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prog_solutions (
  username    VARCHAR(50),
  exercise_id INT,
  solution    TEXT,

  PRIMARY KEY (username, exercise_id),
  FOREIGN KEY (exercise_id) REFERENCES prog_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Question

CREATE TABLE IF NOT EXISTS quizzes (
  id       INT PRIMARY KEY,
  title    VARCHAR(50),
  author   VARCHAR(50),
  ex_text  TEXT,
  ex_state ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  theme    VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS questions (
  id            INT PRIMARY KEY,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  question_type ENUM ('CHOICE', 'FREETEXT', 'FILLOUT')               DEFAULT 'CHOICE'
);

# Spread

CREATE TABLE IF NOT EXISTS spread_exercises (
  id                INT PRIMARY KEY,
  title             VARCHAR(50),
  author            VARCHAR(50),
  ex_text           TEXT,
  ex_state          ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  sample_filename   VARCHAR(50),
  template_filename VARCHAR(50)
);

# Sql

CREATE TABLE IF NOT EXISTS sql_scenarioes (
  id         INT PRIMARY KEY,
  title      VARCHAR(50),
  author     VARCHAR(50),
  ex_text    TEXT,
  ex_state   ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  shortName  VARCHAR(50),
  scriptFile VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sql_exercises (
  id            INT,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED')    DEFAULT 'RESERVED',

  scenario_id   INT,
  tags          TEXT,
  exercise_type ENUM ('SELECT', 'CREATE', 'UPDATE', 'INSERT', 'DELETE') DEFAULT 'SELECT',
  hint          TEXT,

  PRIMARY KEY (id, scenario_id),
  FOREIGN KEY (scenario_id) REFERENCES sql_scenarioes (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sql_samples (
  id          INT,
  exercise_id INT,
  scenario_id INT,
  sample      TEXT,

  PRIMARY KEY (id, exercise_id, scenario_id),
  FOREIGN KEY (exercise_id, scenario_id) REFERENCES sql_exercises (id, scenario_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sql_solutions (
  username    VARCHAR(50),
  scenario_id INT,
  exercise_id INT,
  solution    TEXT,

  PRIMARY KEY (username, scenario_id, exercise_id),
  FOREIGN KEY (username) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (exercise_id, scenario_id) REFERENCES sql_exercises (id, scenario_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Uml

CREATE TABLE IF NOT EXISTS uml_exercises (
  id             INT PRIMARY KEY,
  title          VARCHAR(50),
  author         VARCHAR(50),
  ex_text        TEXT,
  ex_state       ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  class_sel_text TEXT,
  diag_draw_text TEXT
);

CREATE TABLE IF NOT EXISTS uml_ignore (
  exercise_id INT,
  to_ignore   VARCHAR(30),

  PRIMARY KEY (exercise_id, to_ignore),
  FOREIGN KEY (exercise_id) REFERENCES uml_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_mappings (
  exercise_id   INT,
  mapping_key   VARCHAR(50),
  mapping_value VARCHAR(50),

  PRIMARY KEY (exercise_id, mapping_key),
  FOREIGN KEY (exercise_id) REFERENCES uml_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_sol_classes (
  exercise_id INT,
  class_name  VARCHAR(50),
  class_type  ENUM ('CLASS', 'INTERFACE', 'ABSTRACT') DEFAULT 'CLASS',

  PRIMARY KEY (exercise_id, class_name),
  FOREIGN KEY (exercise_id) REFERENCES uml_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_sol_classes_attrs (
  exercise_id INT,
  class_name  VARCHAR(50),
  attr_name   VARCHAR(50),
  attr_type   VARCHAR(50),

  PRIMARY KEY (exercise_id, class_name, attr_name),
  FOREIGN KEY (exercise_id, class_name) REFERENCES uml_sol_classes (exercise_id, class_name)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_sol_classes_methods (
  exercise_id INT,
  class_name  VARCHAR(50),
  method_name VARCHAR(50),
  returns     VARCHAR(50),

  PRIMARY KEY (exercise_id, class_name, method_name, returns),
  FOREIGN KEY (exercise_id, class_name) REFERENCES uml_sol_classes (exercise_id, class_name)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_sol_impls (
  exercise_id INT,
  sub_class   VARCHAR(50),
  super_class VARCHAR(50),

  PRIMARY KEY (exercise_id, sub_class, super_class),
  FOREIGN KEY (exercise_id) REFERENCES uml_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS uml_sol_assocs (
  exercise_id INT,
  assoc_type  ENUM ('ASSOCICATION', 'AGGREGATION', 'COMPOSITION') DEFAULT 'ASSOCICATION',
  assoc_name  VARCHAR(50),
  first_end   VARCHAR(50),
  first_mult  ENUM ('SINGLE', 'UNBOUND'),
  second_end  VARCHAR(50),
  second_mult ENUM ('SINGLE', 'UNBOUND'),

  PRIMARY KEY (exercise_id, assoc_name, first_end, second_end),
  FOREIGN KEY (exercise_id) REFERENCES uml_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Web

CREATE TABLE IF NOT EXISTS web_exercises (
  id            INT PRIMARY KEY,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  html_text     TEXT,
  has_html_part BOOLEAN,
  js_text       TEXT,
  has_js_part   BOOLEAN
);

CREATE TABLE IF NOT EXISTS html_tasks (
  task_id      INT,
  exercise_id  INT,
  text         TEXT,
  xpath_query  VARCHAR(50),

  text_content VARCHAR(100),

  PRIMARY KEY (task_id, exercise_id),
  FOREIGN KEY (exercise_id) REFERENCES web_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS html_attributes (
  attr_key    VARCHAR(30),
  attr_value  VARCHAR(150),
  task_id     INT,
  exercise_id INT,

  PRIMARY KEY (attr_key, task_id, exercise_id),
  FOREIGN KEY (task_id, exercise_id) REFERENCES html_tasks (task_id, exercise_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS js_tasks (
  task_id            INT,
  exercise_id        INT,
  text               TEXT,
  xpath_query        VARCHAR(50),

  action_type        ENUM ('CLICK', 'FILLOUT') DEFAULT 'CLICK',
  action_xpath_query VARCHAR(50),
  keys_to_send       VARCHAR(100),

  PRIMARY KEY (task_id, exercise_id),
  FOREIGN KEY (exercise_id) REFERENCES web_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS js_conditions (
  condition_id    INT,
  task_id         INT,
  exercise_id     INT,

  xpath_query     VARCHAR(50),
  is_precondition BOOLEAN DEFAULT TRUE,
  awaited_value   VARCHAR(50),

  PRIMARY KEY (condition_id, task_id, exercise_id),
  FOREIGN KEY (task_id, exercise_id) REFERENCES js_tasks (task_id, exercise_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS web_solutions (
  exercise_id INT,
  user_name   VARCHAR(30),
  solution    TEXT,

  PRIMARY KEY (exercise_id, user_name),
  FOREIGN KEY (exercise_id) REFERENCES web_exercises (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (user_name) REFERENCES users (username)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

# Xml

CREATE TABLE IF NOT EXISTS xml_exercises (
  id               INT PRIMARY KEY,
  title            VARCHAR(50),
  author           VARCHAR(50),
  ex_text          TEXT,
  ex_state         ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  exercise_type    ENUM ('XML_XSD', 'XML_DTD', 'XSD_XML', 'DTD_XML')    DEFAULT 'XML_XSD',
  root_node        VARCHAR(30),
  ref_file_content TEXT
);

# --- !Downs

# Xml

DROP TABLE IF EXISTS xml_exercises;

# Web

DROP TABLE IF EXISTS web_solutions;

DROP TABLE IF EXISTS js_conditions;

DROP TABLE IF EXISTS js_tasks;

DROP TABLE IF EXISTS html_attributes;

DROP TABLE IF EXISTS html_tasks;

DROP TABLE IF EXISTS web_exercises;

# Uml

DROP TABLE IF EXISTS uml_sol_assocs;

DROP TABLE IF EXISTS uml_sol_impls;

DROP TABLE IF EXISTS uml_sol_classes_methods;

DROP TABLE IF EXISTS uml_sol_classes_attrs;

DROP TABLE IF EXISTS uml_sol_classes;

DROP TABLE IF EXISTS uml_ignore;

DROP TABLE IF EXISTS uml_mappings;

DROP TABLE IF EXISTS uml_exercises;

# Spread

DROP TABLE IF EXISTS spread_exercises;

# Sql

DROP TABLE IF EXISTS sql_solutions;

DROP TABLE IF EXISTS sql_samples;

DROP TABLE IF EXISTS sql_exercises;

DROP TABLE IF EXISTS sql_scenarioes;

# Questions

DROP TABLE IF EXISTS questions;

DROP TABLE IF EXISTS quizzes;

# Programming

DROP TABLE IF EXISTS prog_solutions;

DROP TABLE IF EXISTS prog_commited_testdata_input;

DROP TABLE IF EXISTS prog_sample_testdata_input;

DROP TABLE IF EXISTS prog_commited_testdata;

DROP TABLE IF EXISTS prog_sample_testdata;

DROP TABLE IF EXISTS prog_samples;

DROP TABLE IF EXISTS prog_exercises;

# Ebnf

DROP TABLE IF EXISTS ebnf_exercises;

# General

DROP TABLE IF EXISTS courses;

DROP TABLE IF EXISTS users;

