create table exercise (
  id                        integer primary key auto_increment,
  title                     varchar(255),
  text                      varchar(255),
  default_solution          varchar(255));

create table student (
  name                      varchar(255) primary key);

create table sub_exercise (
  id                        integer primary key auto_increment,
  title                     varchar(255),
  text                      varchar(255),
  exercise_id               integer,
  default_solution          varchar(255),
  foreign key(exercise_id) references exercise(id) on delete cascade on update cascade);

create table task (
  id                        integer primary key auto_increment,
  exercise_id               integer,
  task_description          varchar(255),
  pts                       integer,
  foreign key(exercise_id) references sub_exercise(id) on delete cascade on update cascade);