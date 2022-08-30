drop database if exists it4all;
create database it4all;

-- create users

drop user if exists it4all_admin;
drop user if exists it4all;

create user it4all_admin with password '1234';
create user it4all with password '1234';

-- grant privileges on database

grant all privileges on database it4all to it4all_admin;
grant connect on database it4all to it4all;
