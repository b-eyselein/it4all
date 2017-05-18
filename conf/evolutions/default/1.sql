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
  is_precondition               tinyint(1) default 0,
  awaited_value                 varchar(255),
  constraint pk_conditions primary key (exercise_id,task_id,condition_id)
);

create table feedback (
  user                          varchar(255) not null,
  tool                          varchar(5) not null,
  sense                         varchar(5),
  used                          varchar(5),
  usability                     varchar(9),
  feedback                      varchar(9),
  fairness                      varchar(9),
  comment                       text,
  constraint ck_feedback_tool check ( tool in ('HTML','CSS','JSWEB','JS','SQL')),
  constraint ck_feedback_sense check ( sense in ('YES','NO','MAYBE')),
  constraint ck_feedback_used check ( used in ('YES','NO','MAYBE')),
  constraint ck_feedback_usability check ( usability in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_feedback check ( feedback in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint ck_feedback_fairness check ( fairness in ('VERY_GOOD','GOOD','NEUTRAL','BAD','VERY_BAD','NO_MARK')),
  constraint pk_feedback primary key (user,tool)
);

create table freetext_answer (
  username                      varchar(255) not null,
  question_id                   integer not null,
  answer                        text,
  constraint pk_freetext_answer primary key (username,question_id)
);

create table freetext_question (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  max_points                    integer,
  constraint pk_freetext_question primary key (id)
);

create table given_answer_question (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  max_points                    integer,
  constraint pk_given_answer_question primary key (id)
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

create table prog_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  function_name                 varchar(255),
  input_count                   integer,
  python_sample                 varchar(255),
  js_sample                     varchar(255),
  constraint pk_prog_exercise primary key (id)
);

create table programming_user (
  name                          varchar(255) not null,
  constraint pk_programming_user primary key (name)
);

create table question_rating (
  question_id                   integer not null,
  username                      varchar(255) not null,
  rating                        integer,
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
  scenario_name                 integer,
  validation                    varchar(255),
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

create table uml_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  author                        varchar(255),
  text                          text,
  class_sel_text                text,
  diag_draw_text                text,
  solution                      text,
  constraint pk_uml_exercise primary key (id)
);

create table uml_implementation (
  sub_class                     varchar(255),
  super_class                   varchar(255)
);

create table users (
  name                          varchar(255) not null,
  role                          varchar(5),
  todo                          varchar(9),
  constraint ck_users_role check ( role in ('USER','ADMIN')),
  constraint ck_users_todo check ( todo in ('SHOW','AGGREGATE','HIDE')),
  constraint pk_users primary key (name)
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
  user_name                     varchar(255) not null,
  exercise_id                   integer not null,
  solution                      text,
  points                        integer,
  constraint pk_web_solution primary key (user_name,exercise_id)
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

alter table answer add constraint fk_answer_question_id foreign key (question_id) references given_answer_question (id) on delete restrict on update restrict;
create index ix_answer_question_id on answer (question_id);

alter table commited_test_data add constraint fk_commited_test_data_user_name foreign key (user_name) references programming_user (name) on delete restrict on update restrict;
create index ix_commited_test_data_user_name on commited_test_data (user_name);

alter table commited_test_data add constraint fk_commited_test_data_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_commited_test_data_exercise_id on commited_test_data (exercise_id);

alter table conditions add constraint fk_conditions_task foreign key (task_id,exercise_id) references js_web_task (task_id,exercise_id) on delete restrict on update restrict;
create index ix_conditions_task on conditions (task_id,exercise_id);

alter table freetext_answer add constraint fk_freetext_answer_username foreign key (username) references question_user (name) on delete restrict on update restrict;
create index ix_freetext_answer_username on freetext_answer (username);

alter table freetext_answer add constraint fk_freetext_answer_question_id foreign key (question_id) references freetext_question (id) on delete restrict on update restrict;
create index ix_freetext_answer_question_id on freetext_answer (question_id);

alter table html_task add constraint fk_html_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_html_task_exercise_id on html_task (exercise_id);

alter table js_web_task add constraint fk_js_web_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_js_web_task_exercise_id on js_web_task (exercise_id);

alter table question_rating add constraint fk_question_rating_question_id foreign key (question_id) references given_answer_question (id) on delete restrict on update restrict;
create index ix_question_rating_question_id on question_rating (question_id);

alter table question_rating add constraint fk_question_rating_username foreign key (username) references question_user (name) on delete restrict on update restrict;
create index ix_question_rating_username on question_rating (username);

alter table sample_test_data add constraint fk_sample_test_data_exercise_id foreign key (exercise_id) references prog_exercise (id) on delete restrict on update restrict;
create index ix_sample_test_data_exercise_id on sample_test_data (exercise_id);

alter table sql_exercise add constraint fk_sql_exercise_scenario_name foreign key (scenario_name) references sql_scenario (id) on delete restrict on update restrict;
create index ix_sql_exercise_scenario_name on sql_exercise (scenario_name);

alter table sql_sample add constraint fk_sql_sample_exercise_id foreign key (exercise_id) references sql_exercise (id) on delete restrict on update restrict;
create index ix_sql_sample_exercise_id on sql_sample (exercise_id);

alter table web_solution add constraint fk_web_solution_user_name foreign key (user_name) references web_user (name) on delete restrict on update restrict;
create index ix_web_solution_user_name on web_solution (user_name);

alter table web_solution add constraint fk_web_solution_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_web_solution_exercise_id on web_solution (exercise_id);


# --- !Downs

alter table answer drop foreign key fk_answer_question_id;
drop index ix_answer_question_id on answer;

alter table commited_test_data drop foreign key fk_commited_test_data_user_name;
drop index ix_commited_test_data_user_name on commited_test_data;

alter table commited_test_data drop foreign key fk_commited_test_data_exercise_id;
drop index ix_commited_test_data_exercise_id on commited_test_data;

alter table conditions drop foreign key fk_conditions_task;
drop index ix_conditions_task on conditions;

alter table freetext_answer drop foreign key fk_freetext_answer_username;
drop index ix_freetext_answer_username on freetext_answer;

alter table freetext_answer drop foreign key fk_freetext_answer_question_id;
drop index ix_freetext_answer_question_id on freetext_answer;

alter table html_task drop foreign key fk_html_task_exercise_id;
drop index ix_html_task_exercise_id on html_task;

alter table js_web_task drop foreign key fk_js_web_task_exercise_id;
drop index ix_js_web_task_exercise_id on js_web_task;

alter table question_rating drop foreign key fk_question_rating_question_id;
drop index ix_question_rating_question_id on question_rating;

alter table question_rating drop foreign key fk_question_rating_username;
drop index ix_question_rating_username on question_rating;

alter table sample_test_data drop foreign key fk_sample_test_data_exercise_id;
drop index ix_sample_test_data_exercise_id on sample_test_data;

alter table sql_exercise drop foreign key fk_sql_exercise_scenario_name;
drop index ix_sql_exercise_scenario_name on sql_exercise;

alter table sql_sample drop foreign key fk_sql_sample_exercise_id;
drop index ix_sql_sample_exercise_id on sql_sample;

alter table web_solution drop foreign key fk_web_solution_user_name;
drop index ix_web_solution_user_name on web_solution;

alter table web_solution drop foreign key fk_web_solution_exercise_id;
drop index ix_web_solution_exercise_id on web_solution;

drop table if exists answer;

drop table if exists commited_test_data;

drop table if exists conditions;

drop table if exists feedback;

drop table if exists freetext_answer;

drop table if exists freetext_question;

drop table if exists given_answer_question;

drop table if exists html_task;

drop table if exists js_web_task;

drop table if exists prog_exercise;

drop table if exists programming_user;

drop table if exists question_rating;

drop table if exists question_user;

drop table if exists quiz;

drop table if exists sample_test_data;

drop table if exists spread_exercise;

drop table if exists sql_exercise;

drop table if exists sql_sample;

drop table if exists sql_scenario;

drop table if exists uml_exercise;

drop table if exists uml_implementation;

drop table if exists users;

drop table if exists web_exercise;

drop table if exists web_solution;

drop table if exists web_user;

drop table if exists xml_exercise;

