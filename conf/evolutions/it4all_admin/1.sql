-- !Ups

create table if not exists users (
  username      varchar(100) not null primary key,
  maybe_pw_hash varchar(100)
);

-- grant privileges...

grant all privileges on all tables in schema public to it4all_admin;
grant all privileges on all sequences in schema public to it4all_admin;

grant select, update, insert, delete on all tables in schema public to it4all;
grant select, usage on all sequences in schema public to it4all;

-- !Downs

drop table if exists users;
