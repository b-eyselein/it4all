CREATE TABLE IF NOT EXISTS users
(
   username    varchar(20) PRIMARY KEY,
   firstname   varchar(50) NOT NULL,
   lastname    varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS phone (
 username  varchar(20),
 phonetype  ENUM('work', 'home', 'mobile'),
 phonenumber  varchar(20)  NOT NULL,
 PRIMARY KEY(username, phonetype),
 FOREIGN KEY(username) REFERENCES users(username)
);

insert into users (`username`, `firstname`, `lastname`) values
	('m_martin', 'Martin', 'Mustermann'),
	('m_max', 'Max', 'Mustermann'),
	('f_martina', 'Martina', 'Musterfrau'),
	('f_mona', 'Mona', 'Musterfrau')
	on duplicate key update firstname = VALUES(firstname);

insert into phone (`username`, `phonetype`, `phonenumber`) values
	('m_martin', 'work', '0123456789'),
	('m_martin', 'home', '1234567890'),
	('f_martina', 'mobile', '9876543210')
	on duplicate key update phonenumber = VALUES(phonenumber);