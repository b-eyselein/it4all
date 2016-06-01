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

create table exercise (
  type                          varchar(31) not null,
  id                            integer auto_increment not null,
  title                         varchar(255),
  exercisetext                  varchar(2000),
  file_name                     varchar(255),
  default_solution              varchar(255),
  function_name                 varchar(255),
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

create table js_test (
  id                            integer auto_increment not null,
  awaited_result                varchar(255),
  exercise_id                   integer,
  constraint pk_js_test primary key (id)
);

create table js_testvalue (
  id                            integer auto_increment not null,
  value                         varchar(255),
  test_id                       integer,
  constraint pk_js_testvalue primary key (id)
);

create table task (
  id                            integer not null,
  exercise_id                   integer not null,
  taskdesc                      varchar(2000),
  xpath_query_name              varchar(255),
  defining_attribute            varchar(255),
  attributes                    varchar(255),
  constraint pk_task primary key (id,exercise_id)
);

create table users (
  name                          varchar(255) not null,
  constraint pk_users primary key (name)
);

create table xmlexercise (
  id                            integer auto_increment not null,
  title                         varchar(255),
  exercisetype                  integer,
  referencefilename             varchar(100),
  exercisetext                  varchar(1000),
  constraint ck_xmlexercise_exercisetype check (exerciseType in (0,1,2,3)),
  constraint pk_xmlexercise primary key (id)
);

alter table childtask add constraint fk_childtask_task foreign key (task_id,exercise_id) references task (id,exercise_id) on delete restrict on update restrict;
create index ix_childtask_task on childtask (task_id,exercise_id);

alter table grading add constraint fk_grading_user_name foreign key (user_name) references users (name) on delete restrict on update restrict;
create index ix_grading_user_name on grading (user_name);

alter table grading add constraint fk_grading_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_grading_exercise_id on grading (exercise_id);

alter table js_test add constraint fk_js_test_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_js_test_exercise_id on js_test (exercise_id);

alter table js_testvalue add constraint fk_js_testvalue_test_id foreign key (test_id) references js_test (id) on delete restrict on update restrict;
create index ix_js_testvalue_test_id on js_testvalue (test_id);

alter table task add constraint fk_task_exercise_id foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_task_exercise_id on task (exercise_id);


# --- !Downs

alter table childtask drop foreign key fk_childtask_task;
drop index ix_childtask_task on childtask;

alter table grading drop foreign key fk_grading_user_name;
drop index ix_grading_user_name on grading;

alter table grading drop foreign key fk_grading_exercise_id;
drop index ix_grading_exercise_id on grading;

alter table js_test drop foreign key fk_js_test_exercise_id;
drop index ix_js_test_exercise_id on js_test;

alter table js_testvalue drop foreign key fk_js_testvalue_test_id;
drop index ix_js_testvalue_test_id on js_testvalue;

alter table task drop foreign key fk_task_exercise_id;
drop index ix_task_exercise_id on task;

drop table if exists childtask;

drop table if exists exercise;

drop table if exists feedback;

drop table if exists grading;

drop table if exists js_test;

drop table if exists js_testvalue;

drop table if exists task;

drop table if exists users;

drop table if exists xmlexercise;

