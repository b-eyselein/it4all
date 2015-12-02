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

create table exercise (
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      varchar(255),
  constraint pk_exercise primary key (id))
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

create table student (
  name                      varchar(255) not null,
  constraint pk_student primary key (name))
;

create table task (
  id                        integer auto_increment not null,
  exercise_id               integer,
  task_description          varchar(255),
  pts                       integer,
  result_type               integer,
  tag_name                  varchar(255),
  element_name              varchar(255),
  attributes                varchar(255),
  constraint ck_task_result_type check (result_type in (0,1,2)),
  constraint pk_task primary key (id))
;

alter table grading add constraint fk_grading_student_1 foreign key (student_name) references student (name) on delete restrict on update restrict;
create index ix_grading_student_1 on grading (student_name);
alter table grading add constraint fk_grading_exercise_2 foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_grading_exercise_2 on grading (exercise_id);
alter table task add constraint fk_task_exercise_3 foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_task_exercise_3 on task (exercise_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table administrator;

drop table excel_exercise;

drop table exercise;

drop table feedback;

drop table grading;

drop table student;

drop table task;

SET FOREIGN_KEY_CHECKS=1;

