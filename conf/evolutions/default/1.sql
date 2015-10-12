# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table exercise (
  id                        integer not null,
  title                     varchar(255),
  text                      varchar(255),
  default_solution          varchar(255),
  constraint pk_exercise primary key (id))
;

create table student (
  name                      varchar(255) not null,
  constraint pk_student primary key (name))
;

create table sub_exercise (
  id                        integer not null,
  title                     varchar(255),
  text                      varchar(255),
  exercise_id               integer,
  default_solution          varchar(255),
  constraint pk_sub_exercise primary key (id))
;

create table task (
  id                        integer not null,
  exercise_id               integer,
  task_description          varchar(255),
  pts                       integer,
  constraint pk_task primary key (id))
;

create sequence exercise_seq;

create sequence student_seq;

create sequence sub_exercise_seq;

create sequence task_seq;

alter table sub_exercise add constraint fk_sub_exercise_exercise_1 foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_sub_exercise_exercise_1 on sub_exercise (exercise_id);
alter table task add constraint fk_task_exercise_2 foreign key (exercise_id) references sub_exercise (id) on delete restrict on update restrict;
create index ix_task_exercise_2 on task (exercise_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists exercise;

drop table if exists student;

drop table if exists sub_exercise;

drop table if exists task;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists exercise_seq;

drop sequence if exists student_seq;

drop sequence if exists sub_exercise_seq;

drop sequence if exists task_seq;

