# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table exercise (
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      varchar(255),
  constraint pk_exercise primary key (id))
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

alter table task add constraint fk_task_exercise_1 foreign key (exercise_id) references exercise (id) on delete restrict on update restrict;
create index ix_task_exercise_1 on task (exercise_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table exercise;

drop table student;

drop table task;

SET FOREIGN_KEY_CHECKS=1;

