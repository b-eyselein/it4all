create table student (
  name                      varchar(255) primary key);

create table exercise (
  id                        integer primary key auto_increment,
  title                     varchar(255),
  text                      varchar(1000));

create table task (
  id                        integer,
  exercise_id               integer,
  task_description          varchar(255),
  pts                       integer,
  result_type               integer,
  tag_name                  varchar(255),
  element_name              varchar(255),
  attributes                varchar(255),
  constraint ck_task_result_type check (result_type in (0,1,2)),
  primary key(exercise_id, id),
  foreign key(exercise_id) references exercise(id) on delete cascade on update cascade);