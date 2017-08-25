# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table answer (
  question_id                   integer not null,
  id                            integer not null,
  text                          text,
  correctness                   varchar(8),
  constraint ck_answer_correctness check ( correctness in ('CORRECT','OPTIONAL','WRONG')),
  constraint pk_answer primary key (question_id,id)
);

create table commited_test_data (
  user_name                     varchar(255) not null,
  exercise_id                   integer not null,
  test_id                       integer not null,
  inputs                        text,
  output                        varchar(255),
  approval_state                varchar(8),
  constraint ck_commited_test_data_approval_state check ( approval_state in ('CREATED','SAVED','ACCEPTED','APPROVED')),
  constraint pk_commited_test_data primary key (user_name,exercise_id,test_id)
);

create table conditions (
  exercise_id                   integer not null,
  task_id                       integer not null,
  condition_id                  integer not null,
  xpath_query                   varchar(255),
  is_precondition               tinyint(1) default 0 not null,
  awaited_value                 varchar(255),
  constraint pk_conditions primary key (exercise_id,task_id,condition_id)
);

create table course (
  id                            integer auto_increment not null,
  name                          varchar(255),
  constraint pk_course primary key (id)
);

create table course_role (
  user_name                     varchar(255) not null,
  course_id                     integer not null,
  role                          varchar(10),
  constraint ck_course_role_role check ( role in ('USER','ADMIN','SUPERADMIN')),
  constraint pk_course_role primary key (user_name,course_id)
);

create table feedback (
  user                          varchar(255) not null,
  tool                          varchar(5) not null,
  sense                         varchar(9),
  used                          varchar(9),
  usability                     varchar(9),
  feedback                      varchar(9),
  fairness                      varchar(9),
  comment                       text,
  constraint ck_feedback_tool check ( tool in ('HTML','CSS','JSWEB','JS','SQL')),
  constraint ck_feedback_sense check ( sense in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_used check ( used in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_usability check ( usability in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_feedback check ( feedback in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_fairness check ( fairness in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint pk_feedback primary key (user,tool)
);

create table html_task (
  task_id                       integer not null,
  exercise_id                   integer not null,
  text                          text,
  xpath_query                   varchar(255),
  attributes                    varchar(255),
  text_content                  varchar(255),
  constraint pk_html_task primary key (task_id,exercise_id)
);

create table js_web_task (
  task_id                       integer not null,
  exercise_id                   integer not null,
  text                          text,
  xpath_query                   varchar(255),
  actiontype                    varchar(7),
  action_xpath_query            varchar(255),
  keys_to_send                  varchar(255),
  constraint ck_js_web_task_actiontype check ( actiontype in ('CLICK','FILLOUT')),
  constraint pk_js_web_task primary key (task_id,exercise_id)
);

create table mapping (
  mapped_key                    varchar(255),
  mapped_value                  varchar(255),
  exercise_id                   integer
);

create table prog_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  function_name                 varchar(255),
  input_count                   integer not null,
  constraint pk_prog_exercise primary key (id)
);

create table prog_sample (
  exercise_id                   integer not null,
  language                      integer not null,
  sample                        varchar(255),
  constraint ck_prog_sample_language check ( language in (0,1)),
  constraint pk_prog_sample primary key (exercise_id,language)
);

create table prog_solution (
  user_name                     varchar(255) not null,
  exercise_id                   integer not null,
  language                      integer not null,
  sol                           text,
  constraint ck_prog_solution_language check ( language in (0,1)),
  constraint pk_prog_solution primary key (user_name,exercise_id,language)
);

create table prog_user (
  name                          varchar(255) not null,
  constraint pk_prog_user primary key (name)
);

create table question (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  max_points                    integer not null,
  question_type                 integer,
  constraint ck_question_question_type check ( question_type in (0,1,2)),
  constraint pk_question primary key (id)
);

create table question_quiz (
  question_id                   integer not null,
  quiz_id                       integer not null,
  constraint pk_question_quiz primary key (question_id,quiz_id)
);

create table question_rating (
  question_id                   integer not null,
  username                      varchar(255) not null,
  rating                        integer not null,
  constraint pk_question_rating primary key (question_id,username)
);

create table question_user (
  name                          varchar(255) not null,
  constraint pk_question_user primary key (name)
);

create table quiz (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  theme                         varchar(255),
  constraint pk_quiz primary key (id)
);

create table sample_test_data (
  exercise_id                   integer not null,
  test_id                       integer not null,
  inputs                        text,
  output                        varchar(255),
  constraint pk_sample_test_data primary key (exercise_id,test_id)
);

create table spread_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  sample_filename               varchar(255),
  template_filename             varchar(255),
  constraint pk_spread_exercise primary key (id)
);

create table sql_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  exercise_type                 varchar(6),
  scenario_id                   integer,
  tags                          varchar(255),
  hint                          varchar(255),
  constraint ck_sql_exercise_exercise_type check ( exercise_type in ('SELECT','CREATE','UPDATE','DELETE','INSERT')),
  constraint pk_sql_exercise primary key (id)
);

create table sql_sample (
  exercise_id                   integer not null,
  sample_id                     integer not null,
  sample                        varchar(255),
  constraint pk_sql_sample primary key (exercise_id,sample_id)
);

create table sql_scenario (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  short_name                    varchar(255),
  script_file                   varchar(255),
  constraint pk_sql_scenario primary key (id)
);

create table sql_solution (
  user_name                     varchar(255) not null,
  exercise_id                   integer not null,
  sol                           text,
  points                        integer not null,
  constraint pk_sql_solution primary key (user_name,exercise_id)
);

create table sql_user (
  name                          varchar(255) not null,
  constraint pk_sql_user primary key (name)
);

create table uml_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  class_sel_text                text,
  diag_draw_text                text,
  solution                      text,
  to_ignore                     varchar(255),
  constraint pk_uml_exercise primary key (id)
);

create table uml_implementation (
  sub_class                     varchar(255),
  super_class                   varchar(255)
);

create table users (
  name                          varchar(255) not null,
  std_role                      varchar(10),
  todo                          varchar(9),
  constraint ck_users_std_role check ( std_role in ('USER','ADMIN','SUPERADMIN')),
  constraint ck_users_todo check ( todo in ('SHOW','AGGREGATE','HIDE')),
  constraint pk_users primary key (name)
);

create table user_answer (
  username                      varchar(255) not null,
  question_id                   integer not null,
  text                          text,
  constraint pk_user_answer primary key (username,question_id)
);

create table web_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  html_text                     text,
  js_text                       text,
  constraint pk_web_exercise primary key (id)
);

create table web_solution (
  username                      varchar(255) not null,
  exercise_id                   integer not null,
  sol                           text,
  points                        integer not null,
  user_name                     varchar(255),
  constraint pk_web_solution primary key (username,exercise_id)
);

create table web_user (
  name                          varchar(255) not null,
  constraint pk_web_user primary key (name)
);

create table xml_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  fixed_start                   text,
  exercise_type                 varchar(7),
  reference_file_name           varchar(255),
  constraint ck_xml_exercise_exercise_type check ( exercise_type in ('XML_XSD','XML_DTD','XSD_XML','DTD_XML')),
  constraint pk_xml_exercise primary key (id)
);

alter table answer add constraint fk_answer_question_id foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_answer_question_id on answer (question_id);

alter table commited_test_data add constraint fk_commited_test_data_user_name foreign key (user_name) references prog_user (name) on delete restrict on update restrict;
create index ix_commited_test_data_user_name on commited_test_data (user_name);

alter table commited_test_data add constraint fk_commited_test_data_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_commited_test_data_exercise_id on commited_test_data (exercise_id);

alter table conditions add constraint fk_conditions_task foreign key (task_id,exercise_id) references js_web_task (task_id,exercise_id) on delete restrict on update restrict;
create index ix_conditions_task on conditions (task_id,exercise_id);

alter table course_role add constraint fk_course_role_user_name foreign key (user_name) references users (name) on delete restrict on update restrict;
create index ix_course_role_user_name on course_role (user_name);

alter table course_role add constraint fk_course_role_course_id foreign key (course_id) references course (id) on delete restrict on update restrict;
create index ix_course_role_course_id on course_role (course_id);

alter table html_task add constraint fk_html_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_html_task_exercise_id on html_task (exercise_id);

alter table js_web_task add constraint fk_js_web_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_js_web_task_exercise_id on js_web_task (exercise_id);

alter table mapping add constraint fk_mapping_exercise_id foreign key (exercise_id) references uml_exercise (id) on delete restrict on update restrict;
create index ix_mapping_exercise_id on mapping (exercise_id);

alter table prog_sample add constraint fk_prog_sample_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_prog_sample_exercise_id on prog_sample (exercise_id);

alter table prog_solution add constraint fk_prog_solution_user_name foreign key (user_name) references prog_user (name) on delete restrict on update restrict;
create index ix_prog_solution_user_name on prog_solution (user_name);

alter table prog_solution add constraint fk_prog_solution_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_prog_solution_exercise_id on prog_solution (exercise_id);

alter table question_quiz add constraint fk_question_quiz_question foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_question_quiz_question on question_quiz (question_id);

alter table question_quiz add constraint fk_question_quiz_quiz foreign key (quiz_id) references quiz (id) on delete restrict on update restrict;
create index ix_question_quiz_quiz on question_quiz (quiz_id);

alter table question_rating add constraint fk_question_rating_question_id foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_question_rating_question_id on question_rating (question_id);

alter table question_rating add constraint fk_question_rating_username foreign key (username) references question_user (name) on delete restrict on update restrict;
create index ix_question_rating_username on question_rating (username);

alter table sample_test_data add constraint fk_sample_test_data_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_sample_test_data_exercise_id on sample_test_data (exercise_id);

alter table sql_exercise add constraint fk_sql_exercise_scenario_id foreign key (scenario_id) references sql_scenario (id) on delete restrict on update restrict;
create index ix_sql_exercise_scenario_id on sql_exercise (scenario_id);

alter table sql_sample add constraint fk_sql_sample_exercise_id foreign key (exercise_id) references sql_exercise (id) on delete restrict on update restrict;
create index ix_sql_sample_exercise_id on sql_sample (exercise_id);

alter table sql_solution add constraint fk_sql_solution_user_name foreign key (user_name) references sql_user (name) on delete restrict on update restrict;
create index ix_sql_solution_user_name on sql_solution (user_name);

alter table sql_solution add constraint fk_sql_solution_exercise_id foreign key (exercise_id) references sql_exercise (id) on delete restrict on update restrict;
create index ix_sql_solution_exercise_id on sql_solution (exercise_id);

alter table user_answer add constraint fk_user_answer_username foreign key (username) references question_user (name) on delete restrict on update restrict;
create index ix_user_answer_username on user_answer (username);

alter table user_answer add constraint fk_user_answer_question_id foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_user_answer_question_id on user_answer (question_id);

alter table web_solution add constraint fk_web_solution_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_web_solution_exercise_id on web_solution (exercise_id);

alter table web_solution add constraint fk_web_solution_user_name foreign key (user_name) references web_user (name) on delete restrict on update restrict;
create index ix_web_solution_user_name on web_solution (user_name);


# --- !Downs

alter table answer drop foreign key fk_answer_question_id;
drop index ix_answer_question_id on answer;

alter table commited_test_data drop foreign key fk_commited_test_data_user_name;
drop index ix_commited_test_data_user_name on commited_test_data;

alter table commited_test_data drop foreign key fk_commited_test_data_exercise_id;
drop index ix_commited_test_data_exercise_id on commited_test_data;

alter table conditions drop foreign key fk_conditions_task;
drop index ix_conditions_task on conditions;

alter table course_role drop foreign key fk_course_role_user_name;
drop index ix_course_role_user_name on course_role;

alter table course_role drop foreign key fk_course_role_course_id;
drop index ix_course_role_course_id on course_role;

alter table html_task drop foreign key fk_html_task_exercise_id;
drop index ix_html_task_exercise_id on html_task;

alter table js_web_task drop foreign key fk_js_web_task_exercise_id;
drop index ix_js_web_task_exercise_id on js_web_task;

alter table mapping drop foreign key fk_mapping_exercise_id;
drop index ix_mapping_exercise_id on mapping;

alter table prog_sample drop foreign key fk_prog_sample_exercise_id;
drop index ix_prog_sample_exercise_id on prog_sample;

alter table prog_solution drop foreign key fk_prog_solution_user_name;
drop index ix_prog_solution_user_name on prog_solution;

alter table prog_solution drop foreign key fk_prog_solution_exercise_id;
drop index ix_prog_solution_exercise_id on prog_solution;

alter table question_quiz drop foreign key fk_question_quiz_question;
drop index ix_question_quiz_question on question_quiz;

alter table question_quiz drop foreign key fk_question_quiz_quiz;
drop index ix_question_quiz_quiz on question_quiz;

alter table question_rating drop foreign key fk_question_rating_question_id;
drop index ix_question_rating_question_id on question_rating;

alter table question_rating drop foreign key fk_question_rating_username;
drop index ix_question_rating_username on question_rating;

alter table sample_test_data drop foreign key fk_sample_test_data_exercise_id;
drop index ix_sample_test_data_exercise_id on sample_test_data;

alter table sql_exercise drop foreign key fk_sql_exercise_scenario_id;
drop index ix_sql_exercise_scenario_id on sql_exercise;

alter table sql_sample drop foreign key fk_sql_sample_exercise_id;
drop index ix_sql_sample_exercise_id on sql_sample;

alter table sql_solution drop foreign key fk_sql_solution_user_name;
drop index ix_sql_solution_user_name on sql_solution;

alter table sql_solution drop foreign key fk_sql_solution_exercise_id;
drop index ix_sql_solution_exercise_id on sql_solution;

alter table user_answer drop foreign key fk_user_answer_username;
drop index ix_user_answer_username on user_answer;

alter table user_answer drop foreign key fk_user_answer_question_id;
drop index ix_user_answer_question_id on user_answer;

alter table web_solution drop foreign key fk_web_solution_exercise_id;
drop index ix_web_solution_exercise_id on web_solution;

alter table web_solution drop foreign key fk_web_solution_user_name;
drop index ix_web_solution_user_name on web_solution;

drop table if exists answer;

drop table if exists commited_test_data;

drop table if exists conditions;

drop table if exists course;

drop table if exists course_role;

drop table if exists feedback;

drop table if exists html_task;

drop table if exists js_web_task;

drop table if exists mapping;

drop table if exists prog_exercise;

drop table if exists prog_sample;

drop table if exists prog_solution;

drop table if exists prog_user;

drop table if exists question;

drop table if exists question_quiz;

drop table if exists question_rating;

drop table if exists question_user;

drop table if exists quiz;

drop table if exists sample_test_data;

drop table if exists spread_exercise;

drop table if exists sql_exercise;

drop table if exists sql_sample;

drop table if exists sql_scenario;

drop table if exists sql_solution;

drop table if exists sql_user;

drop table if exists uml_exercise;

drop table if exists uml_implementation;

drop table if exists users;

drop table if exists user_answer;

drop table if exists web_exercise;

drop table if exists web_solution;

drop table if exists web_user;

drop table if exists xml_exercise;

