# --- !Ups

insert into users (user_type, username, std_role, showHideAgg)
values (0, 'developer', 'RoleSuperAdmin', 'AGGREGATE')
on duplicate key update std_role = values(std_role);

insert into pw_hashes (username, pw_hash)
values ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG')
on duplicate key update pw_hash = values(pw_hash);

# --- !Downs

# Xml

delete ignore
from xml_exercise_reviews;

delete ignore
from xml_solutions;

delete ignore
from xml_samples;

delete ignore
from xml_exercises;

delete ignore
from xml_collections;

# Web

delete ignore
from web_exercise_reviews;

delete ignore
from web_solutions;

delete ignore
from web_sample_solutions;

delete ignore
from js_conditions;

delete ignore
from js_tasks;

delete ignore
from html_attributes;

delete ignore
from html_tasks;

delete ignore
from web_exercises;

delete ignore
from web_collections;

# Uml

delete ignore
from uml_exercise_reviews;

delete ignore
from uml_solutions;

delete ignore
from uml_sample_solutions;

delete ignore
from uml_mappings;

delete ignore
from uml_to_ignore;

delete ignore
from uml_exercises;

delete ignore
from uml_collections;

# Sql

delete ignore
from sql_solutions;

delete ignore
from sql_samples;

delete ignore
from sql_exercises;

delete ignore
from sql_scenarioes;

# Rose

delete ignore
from rose_exercise_reviews;

delete ignore
from rose_solutions;

delete ignore
from rose_samples;

delete ignore
from rose_inputs;

delete ignore
from rose_exercises;

delete ignore
from rose_collections;

# Regex

delete ignore
from regex_exercise_reviews;

delete ignore
from regex_solutions;

delete ignore
from regex_test_data;

delete ignore
from regex_sample_solutions;

delete ignore
from regex_exercises;

delete ignore
from regex_collections;

# Programming

delete ignore
from prog_exercise_reviews;

delete ignore
from prog_solutions;

delete ignore
from prog_uml_cd_parts;

delete ignore
from prog_commited_testdata;

delete ignore
from prog_sample_testdata;

delete ignore
from prog_sample_solutions;

delete ignore
from prog_input_types;

delete ignore
from prog_exercises;

delete ignore
from prog_collections;

# General

delete ignore
from learning_path_sections;

delete ignore
from learning_paths;

delete ignore
from feedback;

delete ignore
from users_in_courses;

delete ignore
from courses;

delete ignore
from pw_hashes;

delete ignore
from users;

