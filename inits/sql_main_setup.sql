DROP USER IF EXISTS 'it4all';
CREATE USER 'it4all'@'%';

CREATE DATABASE IF NOT EXISTS it4all;
CREATE DATABASE IF NOT EXISTS it4all_prog;

GRANT ALL ON *.* TO 'it4all'@'%' IDENTIFIED BY 'sT8aV#k7';

FLUSH PRIVILEGES;