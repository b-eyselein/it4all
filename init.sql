drop user if exists it4all;
create user it4all identified by '1234';

drop database if exists it4all;
create database it4all;

grant all privileges on it4all.* to it4all;
flush privileges;
