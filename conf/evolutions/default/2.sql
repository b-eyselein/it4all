# --- !Ups

INSERT INTO users (username, pw_hash, std_role, todo) VALUES
  ('bje40dc', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR'),
  ('jok30ni', '', 'RoleAdmin', 'AGGR'),
  ('alg81dm', '', 'RoleAdmin', 'AGGR'),
  ('s319787', '', 'RoleAdmin', 'AGGR'),
  ('s323295', '', 'RoleAdmin', 'AGGR'),
  ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR')
ON DUPLICATE KEY UPDATE std_role = VALUES(std_role);

# --- !Downs

# Xml

DELETE IGNORE FROM xml_exercises;

# Web

DELETE IGNORE FROM web_solutions;

DELETE IGNORE FROM js_conditions;

DELETE IGNORE FROM js_tasks;

DELETE IGNORE FROM html_attributes;

DELETE IGNORE FROM html_tasks;

DELETE IGNORE FROM web_exercises;

# Uml

DELETE IGNORE FROM uml_sol_assocs;

DELETE IGNORE FROM uml_sol_impl;

DELETE IGNORE FROM uml_sol_classes_methods;

DELETE IGNORE FROM uml_sol_classes_attrs;

DELETE IGNORE FROM uml_sol_classes;

DELETE IGNORE FROM uml_ignore;

DELETE IGNORE FROM uml_mappings;

DELETE IGNORE FROM uml_exercises;

# Spread

DELETE IGNORE FROM spread_exercises;

# Sql

DELETE IGNORE FROM sql_exercises;

DELETE IGNORE FROM sql_scenarioes;

# Questions

DELETE IGNORE FROM questions;

DELETE IGNORE FROM quizzes;

# Programming

DELETE IGNORE FROM prog_solutions;

DELETE IGNORE FROM prog_commited_testdata_input;

DELETE IGNORE FROM prog_sample_testdata_input;

DELETE IGNORE FROM prog_commited_testdata;

DELETE IGNORE FROM prog_sample_testdata;

DELETE IGNORE FROM prog_samples;

DELETE IGNORE FROM prog_exercises;

# Ebnf

DELETE IGNORE FROM ebnf_exercises;

# General

DELETE IGNORE FROM courses;

DELETE IGNORE FROM users;

