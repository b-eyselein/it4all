-- HTML 
create table html_exercise (
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
  foreign key(exercise_id)  references html_exercise(id) on delete cascade on update cascade);
  
-- JS
create table js_exercise (
  id                        integer primary key auto_increment,
  name                      varchar(255) not null,
  text                      varchar(255) not null,
  default_solution          varchar(255) not null,
  function_name             varchar(255) not null);

create table js_test (
  id                        integer primary key auto_increment,
  exercise_id               integer not null,
  awaited_result            varchar(255) not null,
  foreign key(exercise_id) references js_exercise(id) on delete cascade on update cascade);

create table js_testvalue (
  id                        integer primary key auto_increment,
  test_id                   integer not null,
  value                     varchar(255) not null,
  foreign key(test_id) references js_test(id) on delete cascade on update cascade);