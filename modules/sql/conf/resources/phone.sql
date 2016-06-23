CREATE TABLE IF NOT EXISTS users
(
   username    varchar(20) PRIMARY KEY,
   firstname   varchar(50) NOT NULL,
   lastname    varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS phone (
   username  varchar(20),
   phonetype  ENUM('work', 'home', 'mobile'),
   phonenumber  varchar(20),
   PRIMARY KEY(username, phonetype),
   FOREIGN KEY(username) REFERENCES users(username)
);

CREATE TABLE IF NOT EXISTS email (
	username varchar(20),
	emailtype ENUM('work', 'private'),
	emailaddress varchar(100),
	PRIMARY KEY(username, emailtype),
	FOREIGN KEY(username) REFERENCES users(username)
);

INSERT INTO users (`username`, `firstname`, `lastname`) VALUES
	('m_martin', 'Martin', 'Mustermann'),
	('m_max', 'Max', 'Mustermann'),
	('f_martina', 'Martina', 'Musterfrau'),
	('f_mona', 'Mona', 'Musterfrau')
	ON DUPLICATE KEY UPDATE firstname = VALUES(firstname);

INSERT INTO phone (`username`, `phonetype`, `phonenumber`) VALUES
	('m_martin', 'work', '0123456789'),
	('m_martin', 'home', '1234567890'),
	('f_martina', 'mobile', '9876543210')
	ON DUPLICATE KEY UPDATE phonenumber = VALUES(phonenumber);
			   
INSERT INTO email (`username`, `emailtype`, `emailaddress`) VALUES
	('m_martin', 'work', 'm.martin@company.com'),
	('m_martin', 'private', 'm.martin@online.com'),
	('f_martina', 'work', 'f.martina@company.com')
	ON DUPLICATE KEY UPDATE emailaddress = VALUES(emailaddress);
	