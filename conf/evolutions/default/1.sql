# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table childtask (
  id                            integer not null,
  task_id                       integer not null,
  exercise_id                   integer,
  tagname                       varchar(255),
  defining_attribute            varchar(255),
  constraint pk_childtask primary key (id,task_id)
);

create table conditions (
  id                            integer auto_increment not null,
  pre_id                        integer,
  post_id                       integer,
  xpathquery                    varchar(255),
  awaitedvalue                  varchar(255),
  constraint pk_conditions primary key (id)
);

create table css_task (
  task_id                       integer not null,
  exercise_id                   integer not null,
  taskdesc                      text,
  xpath_query_name              varchar(255),
  defining_attribute            varchar(255),
  attributes                    varchar(255),
  constraint pk_css_task primary key (task_id,exercise_id)
);

create table exercise (
  type                          varchar(10) not null,
  id                            integer auto_increment not null,
  title                         varchar(255),
  exercisetext                  text,
  constraint pk_exercise primary key (id)
);

create table feedback (
  id                            integer auto_increment not null,
  sinn_html                     integer,
  sinn_excel                    integer,
  nutzen_html                   integer,
  nutzen_excel                  integer,
  bedienung_html                integer,
  bedienung_excel               integer,
  feedback_html                 integer,
  feedback_excel                integer,
  korrektur_html                integer,
  korrektur_excel               integer,
  kommentar_html                varchar(255),
  kommentar_excel               varchar(255),
  constraint ck_feedback_bedienung_html check (bedienung_html in (0,1,2,3,4)),
  constraint ck_feedback_bedienung_excel check (bedienung_excel in (0,1,2,3,4)),
  constraint ck_feedback_feedback_html check (feedback_html in (0,1,2,3,4)),
  constraint ck_feedback_feedback_excel check (feedback_excel in (0,1,2,3,4)),
  constraint ck_feedback_korrektur_html check (korrektur_html in (0,1,2,3,4)),
  constraint ck_feedback_korrektur_excel check (korrektur_excel in (0,1,2,3,4)),
  constraint pk_feedback primary key (id)
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
  taskdesc                      text,
  xpath_query_name              varchar(255),
  defining_attribute            varchar(255),
  attributes                    varchar(255),
  constraint pk_html_task primary key (task_id,exercise_id)
);

create table js_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  declaration                   varchar(255),
  functionname                  varchar(255),
  sample_solution               varchar(255),
  inputtypes                    varchar(255),
  inputcount                    integer,
  returntype                    varchar(9),
  constraint ck_js_exercise_returntype check (returntype in ('BOOLEAN','NUMBER','STRING','SYMBOL','UNDEFINED','NULL','OBJECT')),
  constraint pk_js_exercise primary key (id)
);

create table js_test (
  exercise_id                   integer not null,
  test_id                       integer not null,
  inputs                        text,
  output                        varchar(255),
  constraint pk_js_test primary key (exercise_id,test_id)
);

create table js_web_exercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  text                          text,
  anterior                      text,
  posterior                     text,
  declaration                   varchar(255),
  constraint pk_js_web_exercise primary key (id)
);

create table js_web_test (
  id                            integer auto_increment not null,
  exercise_id                   integer,
  actiontype                    varchar(7),
  xpath_query                   varchar(255),
  keys_to_send                  varchar(255),
  constraint ck_js_web_test_actiontype check (actiontype in ('CLICK','FILLOUT')),
  constraint pk_js_web_test primary key (id)
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
  querytype                     varchar(6),
  id                            integer not null,
  scenario_name                 varchar(255) not null,
  text                          text,
  samples                       text,
  tablename                     varchar(255),
  validation                    varchar(255),
  constraint ck_sql_exercise_querytype check (querytype in ('SELECT','CREATE','UPDATE','DELETE','INSERT')),
  constraint pk_sql_exercise primary key (id,scenario_name)
);

create table sql_scenario (
  short_name                    varchar(255) not null,
  long_name                     varchar(255),
  script_file                   varchar(255),
  constraint pk_sql_scenario primary key (short_name)
);

create table users (
  name                          varchar(255) not null,
  constraint pk_users primary key (name)
);

create table xmlexercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  exercisetype                  varchar(13),
  referencefilename             varchar(100),
  exercisetext                  varchar(1000),
  constraint ck_xmlexercise_exercisetype check (exerciseType in ('XMLAgainstXSD','XMLAgainstDTD','XSDAgainstXML','DTDAgainstXML')),
  constraint pk_xmlexercise primary key (id)
);

alter table childtask add constraint fk_childtask_task foreign key (task_id,exercise_id) references html_task (task_id,exercise_id) on delete restrict on update restrict;
create index ix_childtask_task on childtask (task_id,exercise_id);

alter table conditions add constraint fk_conditions_pre_id foreign key (pre_id) references js_web_test (id) on delete restrict on update restrict;
create index ix_conditions_pre_id on conditions (pre_id);

alter table conditions add constraint fk_conditions_post_id foreign key (post_id) references js_web_test (id) on delete restrict on update restrict;
create index ix_conditions_post_id on conditions (post_id);

alter table css_task add constraint fk_css_task_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_css_task_exercise_id on css_task (exercise_id);

alter table grading add constraint fk_grading_user_name foreign key (user_name) references users (name) on delete restrict on update restrict;
create index ix_grading_user_name on grading (user_name);

alter table grading add constraint fk_grading_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_grading_exercise_id on grading (exercise_id);

alter table html_task add constraint fk_html_task_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_html_task_exercise_id on html_task (exercise_id);

alter table js_test add constraint fk_js_test_exercise_id foreign key (exercise_id) references js_exercise (id) on delete restrict on update restrict;
create index ix_js_test_exercise_id on js_test (exercise_id);

alter table js_web_test add constraint fk_js_web_test_exercise_id foreign key (exercise_id) references js_web_exercise (id) on delete restrict on update restrict;
create index ix_js_web_test_exercise_id on js_web_test (exercise_id);

alter table sql_exercise add constraint fk_sql_exercise_scenario_name foreign key (scenario_name) references sql_scenario (short_name) on delete restrict on update restrict;
create index ix_sql_exercise_scenario_name on sql_exercise (scenario_name);


# --- !Downs

alter table childtask drop foreign key fk_childtask_task;
drop index ix_childtask_task on childtask;

alter table conditions drop foreign key fk_conditions_pre_id;
drop index ix_conditions_pre_id on conditions;

alter table conditions drop foreign key fk_conditions_post_id;
drop index ix_conditions_post_id on conditions;

alter table css_task drop foreign key fk_css_task_exercise_id;
drop index ix_css_task_exercise_id on css_task;

alter table grading drop foreign key fk_grading_user_name;
drop index ix_grading_user_name on grading;

alter table grading drop foreign key fk_grading_exercise_id;
drop index ix_grading_exercise_id on grading;

alter table html_task drop foreign key fk_html_task_exercise_id;
drop index ix_html_task_exercise_id on html_task;

alter table js_test drop foreign key fk_js_test_exercise_id;
drop index ix_js_test_exercise_id on js_test;

alter table js_web_test drop foreign key fk_js_web_test_exercise_id;
drop index ix_js_web_test_exercise_id on js_web_test;

alter table sql_exercise drop foreign key fk_sql_exercise_scenario_name;
drop index ix_sql_exercise_scenario_name on sql_exercise;

drop table if exists childtask;

drop table if exists conditions;

drop table if exists css_task;

drop table if exists exercise;

drop table if exists feedback;

drop table if exists grading;

drop table if exists html_task;

drop table if exists js_exercise;

drop table if exists js_test;

drop table if exists js_web_exercise;

drop table if exists js_web_test;

drop table if exists spread_exercise;

drop table if exists sql_exercise;

drop table if exists sql_scenario;

drop table if exists users;

drop table if exists xmlexercise;

