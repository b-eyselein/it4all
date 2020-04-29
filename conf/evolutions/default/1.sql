# --- !Ups

create table if not exists users (
    username varchar(30)                                      not null primary key,
    pw_hash  varchar(60),
    std_role enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') not null default 'RoleUser'
);

# --- !Downs

drop table if exists users;
