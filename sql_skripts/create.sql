create table student (
  name                      varchar(255) primary key);

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
  foreign key(exercise_id)  references exercise(id) on delete cascade on update cascade);
  
create table grading (
  id                        integer primary key auto_increment,
  student_name              varchar(255) not null,
  exercise_id               integer not null,
  points					integer not null,
  foreign key(student_name) references student(name) on delete cascade on update cascade,
  foreign key(exercise_id)  references exercise(id) on delete cascade on update cascade);
  
create table feedback (
  id                        integer primary key auto_increment,
  sinn_html					integer,
  sinn_excel				integer,
  nutzen_html				integer,
  nutzen_excel				integer,
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
  constraint ck_feedback_korrektur_excel check (korrektur_excel in (0,1,2,3,4)));
  
create table excel_exercise (
  id						integer primary key auto_increment,
  title						varchar(255) not null,
  file_name					varchar(255) not null);