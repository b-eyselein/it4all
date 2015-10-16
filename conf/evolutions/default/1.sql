# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table exercise (
  id                        integer auto_increment not null,
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
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      varchar(255),
  exercise_id               integer,
  default_solution          varchar(255),
  constraint pk_sub_exercise primary key (id))
;

create table task (
  id                        integer auto_increment not null,
  sub_exercise_id           integer,
  task_description          varchar(255),
  pts                       integer,
  constraint pk_task primary key (id))
;

alter table sub_exercise add constraint fk_sub_exercise_exercise_1 foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_sub_exercise_exercise_1 on sub_exercise (exercise_id);
alter table task add constraint fk_task_subExercise_2 foreign key (sub_exercise_id) references sub_exercise (id) on delete restrict on update restrict;
create index ix_task_subExercise_2 on task (sub_exercise_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table exercise;

drop table student;

drop table sub_exercise;

drop table task;

SET FOREIGN_KEY_CHECKS=1;

