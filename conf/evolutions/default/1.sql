# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table administrator (
  name                      varchar(255) not null,
  password                  varchar(255),
  constraint pk_administrator primary key (name))
;

create table excel_exercise (
  id                        integer auto_increment not null,
  title                     varchar(255),
  file_name                 varchar(255),
  constraint pk_excel_exercise primary key (id))
;

create table feedback (
  id                        integer auto_increment not null,
  sinn_html                 integer,
  sinn_excel                integer,
  nutzen_html               integer,
  nutzen_excel              integer,
  bedienung_html            integer,
  bedienung_excel           integer,
  feedback_html             integer,
  feedback_excel            integer,
  korrektur_html            integer,
  korrektur_excel           integer,
  kommentar_html            varchar(255),
  kommentar_excel           varchar(255),
  constraint ck_feedback_bedienung_html check (bedienung_html in (0,1,2,3,4)),
  constraint ck_feedback_bedienung_excel check (bedienung_excel in (0,1,2,3,4)),
  constraint ck_feedback_feedback_html check (feedback_html in (0,1,2,3,4)),
  constraint ck_feedback_feedback_excel check (feedback_excel in (0,1,2,3,4)),
  constraint ck_feedback_korrektur_html check (korrektur_html in (0,1,2,3,4)),
  constraint ck_feedback_korrektur_excel check (korrektur_excel in (0,1,2,3,4)),
  constraint pk_feedback primary key (id))
;

create table grading (
  id                        integer auto_increment not null,
  student_name              varchar(255),
  exercise_id               integer,
  points                    integer,
  constraint pk_grading primary key (id))
;

create table htmlexercise (
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      varchar(255),
  constraint pk_htmlexercise primary key (id))
;

create table js_exercise (
  id                        integer auto_increment not null,
  name                      varchar(255),
  text                      varchar(255),
  default_solution          varchar(255),
  function_name             varchar(255),
  constraint pk_js_exercise primary key (id))
;

create table js_test (
  id                        integer auto_increment not null,
  awaited_result            varchar(255),
  exercise_id               integer,
  constraint pk_js_test primary key (id))
;

create table js_testvalue (
  id                        integer auto_increment not null,
  value                     varchar(255),
  test_id                   integer,
  constraint pk_js_testvalue primary key (id))
;

create table student (
  name                      varchar(255) not null,
  constraint pk_student primary key (name))
;

create table task (
  dtype                     varchar(10) not null,
  id                        integer,
  exerciseid                integer,
  taskDesc                  varchar(255),
  tagName                   varchar(255),
  attributes                varchar(255),
  elemName                  varchar(255),
  title                     varchar(255),
  constraint pk_task primary key (id, exerciseid))
;

alter table grading add constraint fk_grading_student_1 foreign key (student_name) references student (name) on delete restrict on update restrict;
create index ix_grading_student_1 on grading (student_name);
alter table grading add constraint fk_grading_exercise_2 foreign key (exercise_id) references htmlexercise (id) on delete restrict on update restrict;
create index ix_grading_exercise_2 on grading (exercise_id);
alter table js_test add constraint fk_js_test_exercise_3 foreign key (exercise_id) references js_exercise (id) on delete restrict on update restrict;
create index ix_js_test_exercise_3 on js_test (exercise_id);
alter table js_testvalue add constraint fk_js_testvalue_test_4 foreign key (test_id) references js_test (id) on delete restrict on update restrict;
create index ix_js_testvalue_test_4 on js_testvalue (test_id);
alter table task add constraint fk_task_exercise_5 foreign key (exerciseId) references htmlexercise (id) on delete restrict on update restrict;
create index ix_task_exercise_5 on task (exerciseId);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table administrator;

drop table excel_exercise;

drop table feedback;

drop table grading;

drop table htmlexercise;

drop table js_exercise;

drop table js_test;

drop table js_testvalue;

drop table student;

drop table task;

SET FOREIGN_KEY_CHECKS=1;

