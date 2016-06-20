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

INSERT INTO users(`username`, `firstname`, `lastname`)
     VALUES ('m_martin', 'Martin', 'Mustermann'),
            ('m_max', 'Max', 'Mustermann'),
            ('f_martina', 'Martina', 'Musterfrau'),
            ('f_mona', 'Mona', 'Musterfrau')
			on duplicate key update firstname = VALUES(firstname);

INSERT INTO phone(`username`, `phonetype`, `phonenumber`)
        VALUES ('m_martin', 'work', '0123456789'),
               ('m_martin', 'home', '1234567890'),
               ('f_martina', 'mobile', '9876543210')
			   on duplicate key update phonenumber = VALUES(phonenumber);