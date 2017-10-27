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

CREATE TABLE IF NOT EXISTS ebnf_exercises (
  id        INT PRIMARY KEY                                      AUTO_INCREMENT,
  title     VARCHAR(50),
  author    VARCHAR(50),
  ex_text   TEXT,
  ex_state  ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  terminals VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS prog_exercises (
  id            INT PRIMARY KEY                                      AUTO_INCREMENT,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  function_name VARCHAR(30),
  input_count   INT
);

CREATE TABLE IF NOT EXISTS quizzes (
  id       INT PRIMARY KEY                                      AUTO_INCREMENT,
  title    VARCHAR(50),
  author   VARCHAR(50),
  ex_text  TEXT,
  ex_state ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  theme    VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS questions (
  id            INT PRIMARY KEY                                      AUTO_INCREMENT,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  question_type ENUM ('CHOICE', 'FREETEXT', 'FILLOUT')               DEFAULT 'CHOICE'
);

CREATE TABLE IF NOT EXISTS spread_exercises (
  id                INT PRIMARY KEY                                      AUTO_INCREMENT,
  title             VARCHAR(50),
  author            VARCHAR(50),
  ex_text           TEXT,
  ex_state          ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  sample_filename   VARCHAR(50),
  template_filename VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sql_scenarioes (
  id         INT PRIMARY KEY                                      AUTO_INCREMENT,
  title      VARCHAR(50),
  author     VARCHAR(50),
  ex_text    TEXT,
  ex_state   ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  shortName  VARCHAR(50),
  scriptFile VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS sql_exercises (
  id            INT PRIMARY KEY                                         AUTO_INCREMENT,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED')    DEFAULT 'RESERVED',

  exercise_type ENUM ('SELECT', 'CREATE', 'UPDATE', 'INSERT', 'DELETE') DEFAULT 'SELECT',
  tags          VARCHAR(50),
  hint          VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS uml_exercises (
  id             INT PRIMARY KEY                                      AUTO_INCREMENT,
  title          VARCHAR(50),
  author         VARCHAR(50),
  ex_text        TEXT,
  ex_state       ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  class_sel_text TEXT,
  diag_draw_text TEXT,
  solution       TEXT,
  mappings       TEXT,
  ignore_words   TEXT

);

CREATE TABLE IF NOT EXISTS web_exercises (
  id        INT PRIMARY KEY                                      AUTO_INCREMENT,
  title     VARCHAR(50),
  author    VARCHAR(50),
  ex_text   TEXT,
  ex_state  ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  html_text TEXT,
  js_text   TEXT
);

CREATE TABLE IF NOT EXISTS xml_exercises (
  id            INT PRIMARY KEY                                      AUTO_INCREMENT,
  title         VARCHAR(50),
  author        VARCHAR(50),
  ex_text       TEXT,
  ex_state      ENUM ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') DEFAULT 'RESERVED',

  exercise_type ENUM ('XML_XSD', 'XML_DTD', 'XSD_XML', 'DTD_XML')    DEFAULT 'XML_XSD',
  root_node     VARCHAR(30)
);

# --- !Downs

DROP TABLE IF EXISTS xml_exercises;

DROP TABLE IF EXISTS web_exercises;

DROP TABLE IF EXISTS uml_exercises;

DROP TABLE IF EXISTS spread_exercises;

DROP TABLE IF EXISTS sql_exercises;

DROP TABLE IF EXISTS sql_scenario;

DROP TABLE IF EXISTS questions;

DROP TABLE IF EXISTS quizzes;

DROP TABLE IF EXISTS prog_exercises;

DROP TABLE IF EXISTS ebnf_exercises;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS courses;
