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

create table conditions (
  exercise_id                   integer not null,
  task_id                       integer not null,
  id                            integer not null,
  xpathquery                    varchar(255),
  awaitedvalue                  varchar(255),
  is_precond                    tinyint(1) default 0,
  constraint pk_conditions primary key (exercise_id,task_id,id)
);

create table css_task (
  task_id                       integer not null,
  exercise_id                   integer not null,
  text                          text,
  xpath_query                   varchar(255),
  attributes                    varchar(255),
  constraint pk_css_task primary key (task_id,exercise_id)
);

create table exercise_result (
  extype                        varchar(31) not null,
  user_name                     varchar(255)
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

create table grading (
  user_name                     varchar(255) not null,
  exercise_id                   integer not null,
  points                        integer,
  constraint pk_grading primary key (user_name,exercise_id)
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

create table js_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  declaration                   varchar(255),
  functionname                  varchar(255),
  sample_solution               varchar(255),
  inputcount                    integer,
  inputtypes                    varchar(255),
  returntype                    varchar(9),
  constraint ck_js_exercise_returntype check ( returntype in ('BOOLEAN','NUMBER','STRING','SYMBOL','UNDEFINED','NULL','OBJECT')),
  constraint pk_js_exercise primary key (id)
);

create table js_test_data (
  exercise_id                   integer not null,
  test_id                       integer not null,
  inputs                        text,
  output                        varchar(255),
  constraint pk_js_test_data primary key (exercise_id,test_id)
);

create table js_web_task (
  task_id                       integer not null,
  exercise_id                   integer not null,
  text                          text,
  xpath_query                   varchar(255),
  attributes                    varchar(255),
  actiontype                    varchar(7),
  action_xpath_query            varchar(255),
  keys_to_send                  varchar(255),
  constraint ck_js_web_task_actiontype check ( actiontype in ('CLICK','FILLOUT')),
  constraint pk_js_web_task primary key (task_id,exercise_id)
);

create table python_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  declaration                   varchar(255),
  functionname                  varchar(255),
  sample_solution               varchar(255),
  inputcount                    integer,
  constraint pk_python_exercise primary key (id)
);

create table python_test_data (
  exercise_id                   integer not null,
  test_id                       integer not null,
  inputs                        text,
  output                        varchar(255),
  constraint pk_python_test_data primary key (exercise_id,test_id)
);

create table question (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  author                        varchar(255),
  max_points                    integer,
  question_type                 varchar(21),
  constraint ck_question_question_type check ( question_type in ('MULTIPLE','SINGLE','FILLOUT_WITH_ORDER','FILLOUT_WITHOUT_ORDER')),
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
  text                          text,
  theme                         varchar(255),
  constraint pk_quiz primary key (id)
);

create table spread_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  sample_filename               varchar(255),
  template_filename             varchar(255),
  constraint pk_spread_exercise primary key (id)
);

create table sql_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  samples                       text,
  exercisetype                  varchar(6),
  scenario_name                 varchar(255),
  validation                    varchar(255),
  tags                          varchar(255),
  hint                          varchar(255),
  constraint ck_sql_exercise_exercisetype check ( exercisetype in ('SELECT','CREATE','UPDATE','DELETE','INSERT')),
  constraint pk_sql_exercise primary key (id)
);

create table sql_scenario (
  short_name                    varchar(255) not null,
  long_name                     varchar(255),
  script_file                   varchar(255),
  constraint pk_sql_scenario primary key (short_name)
);

create table uml_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  class_sel_text                text,
  diag_draw_text                text,
  diag_draw_help_text           text,
  constraint pk_uml_exercise primary key (id)
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
  text                          text,
  html_text                     text,
  css_text                      text,
  js_text                       text,
  constraint pk_web_exercise primary key (id)
);

create table xml_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  fixed_start                   text,
  exercise_type                 varchar(7),
  reference_file_name           varchar(255),
  constraint ck_xml_exercise_exercise_type check ( exercise_type in ('XML_XSD','XML_DTD','XSD_XML','DTD_XML')),
  constraint pk_xml_exercise primary key (id)
);

alter table answer add constraint fk_answer_question_id foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_answer_question_id on answer (question_id);

alter table conditions add constraint fk_conditions_task foreign key (task_id,exercise_id) references js_web_task (task_id,exercise_id) on delete restrict on update restrict;
create index ix_conditions_task on conditions (task_id,exercise_id);

alter table css_task add constraint fk_css_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_css_task_exercise_id on css_task (exercise_id);

alter table exercise_result add constraint fk_exercise_result_user_name foreign key (user_name) references users (name) on delete restrict on update restrict;
create index ix_exercise_result_user_name on exercise_result (user_name);

alter table grading add constraint fk_grading_user_name foreign key (user_name) references users (name) on delete restrict on update restrict;
create index ix_grading_user_name on grading (user_name);

alter table html_task add constraint fk_html_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_html_task_exercise_id on html_task (exercise_id);

alter table js_test_data add constraint fk_js_test_data_exercise_id foreign key (exercise_id) references js_exercise (id) on delete restrict on update restrict;
create index ix_js_test_data_exercise_id on js_test_data (exercise_id);

alter table js_web_task add constraint fk_js_web_task_exercise_id foreign key (exercise_id) references web_exercise (id) on delete restrict on update restrict;
create index ix_js_web_task_exercise_id on js_web_task (exercise_id);

alter table python_test_data add constraint fk_python_test_data_exercise_id foreign key (exercise_id) references python_exercise (id) on delete restrict on update restrict;
create index ix_python_test_data_exercise_id on python_test_data (exercise_id);

alter table question_quiz add constraint fk_question_quiz_question foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_question_quiz_question on question_quiz (question_id);

alter table question_quiz add constraint fk_question_quiz_quiz foreign key (quiz_id) references quiz (id) on delete restrict on update restrict;
create index ix_question_quiz_quiz on question_quiz (quiz_id);

alter table question_rating add constraint fk_question_rating_question_id foreign key (question_id) references question (id) on delete restrict on update restrict;
create index ix_question_rating_question_id on question_rating (question_id);

alter table question_rating add constraint fk_question_rating_username foreign key (username) references question_user (name) on delete restrict on update restrict;
create index ix_question_rating_username on question_rating (username);

alter table sql_exercise add constraint fk_sql_exercise_scenario_name foreign key (scenario_name) references sql_scenario (short_name) on delete restrict on update restrict;
create index ix_sql_exercise_scenario_name on sql_exercise (scenario_name);


# --- !Downs

alter table answer drop foreign key fk_answer_question_id;
drop index ix_answer_question_id on answer;

alter table conditions drop foreign key fk_conditions_task;
drop index ix_conditions_task on conditions;

alter table css_task drop foreign key fk_css_task_exercise_id;
drop index ix_css_task_exercise_id on css_task;

alter table exercise_result drop foreign key fk_exercise_result_user_name;
drop index ix_exercise_result_user_name on exercise_result;

alter table grading drop foreign key fk_grading_user_name;
drop index ix_grading_user_name on grading;

alter table html_task drop foreign key fk_html_task_exercise_id;
drop index ix_html_task_exercise_id on html_task;

alter table js_test_data drop foreign key fk_js_test_data_exercise_id;
drop index ix_js_test_data_exercise_id on js_test_data;

alter table js_web_task drop foreign key fk_js_web_task_exercise_id;
drop index ix_js_web_task_exercise_id on js_web_task;

alter table python_test_data drop foreign key fk_python_test_data_exercise_id;
drop index ix_python_test_data_exercise_id on python_test_data;

alter table question_quiz drop foreign key fk_question_quiz_question;
drop index ix_question_quiz_question on question_quiz;

alter table question_quiz drop foreign key fk_question_quiz_quiz;
drop index ix_question_quiz_quiz on question_quiz;

alter table question_rating drop foreign key fk_question_rating_question_id;
drop index ix_question_rating_question_id on question_rating;

alter table question_rating drop foreign key fk_question_rating_username;
drop index ix_question_rating_username on question_rating;

alter table sql_exercise drop foreign key fk_sql_exercise_scenario_name;
drop index ix_sql_exercise_scenario_name on sql_exercise;

drop table if exists answer;

drop table if exists conditions;

drop table if exists css_task;

drop table if exists exercise_result;

drop table if exists feedback;

drop table if exists grading;

drop table if exists html_task;

drop table if exists js_exercise;

drop table if exists js_test_data;

drop table if exists js_web_task;

drop table if exists python_exercise;

drop table if exists python_test_data;

drop table if exists question;

drop table if exists question_quiz;

drop table if exists question_rating;

drop table if exists question_user;

drop table if exists quiz;

drop table if exists spread_exercise;

drop table if exists sql_exercise;

drop table if exists sql_scenario;

drop table if exists uml_exercise;

drop table if exists users;

drop table if exists web_exercise;

drop table if exists xml_exercise;

