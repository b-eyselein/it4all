drop database if exists it4all;
create database it4all;

drop user if exists it4all;
create user it4all with password '1234';
grant all privileges on database it4all to it4all;
