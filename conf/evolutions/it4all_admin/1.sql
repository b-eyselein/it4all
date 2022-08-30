-- !Ups

create table if not exists users (
  username      varchar(100) not null primary key,
  maybe_pw_hash varchar(100)
);

create table if not exists collections (
  tool_id       varchar(20)  not null,
  collection_id int          not null,
  title         varchar(100) not null,

  primary key (tool_id, collection_id),
  unique (tool_id, title)
);

-- grant privileges...

grant all privileges on all tables in schema public to it4all_admin;
grant all privileges on all sequences in schema public to it4all_admin;

grant select, update, insert, delete on all tables in schema public to it4all;
grant select, usage on all sequences in schema public to it4all;

-- !Downs

drop table if exists collections;

drop table if exists users;
