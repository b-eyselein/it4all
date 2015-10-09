# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table html_exercise (
  id                        integer not null,
  exercise_name             varchar(255),
  exercise_text             varchar(255),
  default_solution          varchar(255),
  constraint pk_html_exercise primary key (id))
;

create table student (
  name                      varchar(255) not null,
  constraint pk_student primary key (name))
;

create table task (
  id                        integer not null,
  exercise_id               integer,
  task_description          varchar(255),
  pts                       integer,
  constraint pk_task primary key (id))
;

create sequence html_exercise_seq;

create sequence student_seq;

create sequence task_seq;

alter table task add constraint fk_task_exercise_1 foreign key (exercise_id) references html_exercise (id) on delete restrict on update restrict;
create index ix_task_exercise_1 on task (exercise_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists html_exercise;

drop table if exists student;

drop table if exists task;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists html_exercise_seq;

drop sequence if exists student_seq;

drop sequence if exists task_seq;

