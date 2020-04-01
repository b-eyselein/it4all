create database if not exists it4all;

create user it4all@'%' identified by 'sT8aV#k7';

grant all on it4all.* to it4all@'%';

flush privileges;