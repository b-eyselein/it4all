# --- !Ups

INSERT INTO users (user_type, username, std_role, todo) VALUES
  (0, 's319286', 'RoleUser', 'AGGR'),
  (0, 'bje40dc', 'RoleAdmin', 'AGGR'),
  (0, 'developer', 'RoleSuperAdmin', 'AGGR')
ON DUPLICATE KEY UPDATE std_role = VALUES(std_role);

INSERT INTO pw_hashes (username, pw_hash) VALUES
  ('s319286', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG'),
  ('bje40dc', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG'),
  ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG')
ON DUPLICATE KEY UPDATE pw_hash = VALUES(pw_hash);

# --- !Downs

# Xml

DELETE IGNORE FROM xml_solutions;

DELETE IGNORE FROM xml_exercises;

# Web

DELETE IGNORE FROM web_solutions;

DELETE IGNORE FROM js_conditions;

DELETE IGNORE FROM js_tasks;

DELETE IGNORE FROM html_attributes;

DELETE IGNORE FROM html_tasks;

DELETE IGNORE FROM web_exercises;

# Uml

DELETE IGNORE FROM uml_solutions;

DELETE IGNORE FROM uml_mappings;

DELETE IGNORE FROM uml_exercises;

# Spread

DELETE IGNORE FROM spread_exercises;

# Sql

DELETE IGNORE FROM sql_solutions;

DELETE IGNORE FROM sql_samples;

DELETE IGNORE FROM sql_exercises;

DELETE IGNORE FROM sql_scenarioes;

# Rose

DELETE IGNORE FROM rose_solutions;

DELETE IGNORE FROM rose_samples;

DELETE IGNORE FROM rose_inputs;

DELETE IGNORE FROM rose_exercises;

# Questions

DELETE IGNORE FROM question_answers;

DELETE IGNORE FROM questions;

DELETE IGNORE FROM quizzes;

# Programming

DELETE IGNORE FROM prog_solutions;

DELETE IGNORE FROM prog_uml_cd_parts;

DELETE IGNORE FROM prog_commited_testdata;

DELETE IGNORE FROM prog_sample_testdata;

DELETE IGNORE FROM prog_samples;

DELETE IGNORE FROM prog_input_types;

DELETE IGNORE FROM prog_exercises;

# Mindmap

DELETE IGNORE FROM mindmap_exercises;

# Blanks

DELETE IGNORE FROM blanks_samples;

DELETE IGNORE FROM blanks_exercises;

# General

DELETE IGNORE FROM users_in_courses;

DELETE IGNORE FROM courses;

DELETE IGNORE FROM pw_hashes;

DELETE IGNORE FROM users;

