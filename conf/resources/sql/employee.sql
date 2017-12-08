CREATE TABLE IF NOT EXISTS employee (
  employeeOID INT PRIMARY KEY,
  firstname   VARCHAR(50) NOT NULL,
  lastname    VARCHAR(50) NOT NULL,
  username    VARCHAR(20),
  chefOID     INT,

  FOREIGN KEY (chefOID) REFERENCES employee (employeeOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS phonenumber (
  recordOID   INT PRIMARY KEY,
  employeeOID INT,
  phonetype   ENUM ('work', 'home', 'mobile'),
  phonenumber VARCHAR(20) NOT NULL,

  FOREIGN KEY (employeeOID) REFERENCES employee (employeeOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS emailaddress (
  recordOID    INT PRIMARY KEY,
  employeeOID  INT,
  emailtype    ENUM ('work', 'private'),
  emailaddress VARCHAR(50) NOT NULL,

  FOREIGN KEY (employeeOID) REFERENCES employee (employeeOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobtitle (
  titleOID    INT PRIMARY KEY,
  name        VARCHAR(50) NOT NULL,
  description TEXT
);

CREATE TABLE IF NOT EXISTS department (
  departmentOID INT PRIMARY KEY,
  name          VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS position (
  employeeOID   INT,
  number        INT,
  salary        INT  NOT NULL,
  begin         DATE NOT NULL,
  end           DATE,
  titleOID      INT,
  departmentOID INT,

  PRIMARY KEY (employeeOID, number),
  FOREIGN KEY (employeeOID) REFERENCES employee (employeeOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (titleOID) REFERENCES jobtitle (titleOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (departmentOID) REFERENCES department (departmentOID)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

INSERT IGNORE INTO employee (`employeeOID`, `firstname`, `lastname`, `username`, `chefOID`) VALUES
  (1, 'Max', 'Becker', 'max_becker', NULL),
  (2, 'Hannah', 'Müller', 'hanna_müller', NULL),
  (3, 'Lukas', 'Schuster', 'lukas_schuster', 1),
  (4, 'Ben', 'Schmidt', 'ben_schmidt', 2),
  (5, 'Lena', 'Fischer', 'lena_muster', 1),
  (6, 'Till', 'Koch', 'till_koch', 3),
  (7, 'Nina', 'Richter', 'nina_richter', 3),
  (8, 'Robert', 'Müller', 'robert_müller', 2);

INSERT IGNORE INTO emailaddress (`recordOID`, `employeeOID`, `emailType`, `emailaddress`) VALUES
  (1, 1, 'work', 'max.becker@firma.com'),
  (2, 2, 'work', 'hannah.müller@firma.com');

INSERT IGNORE INTO phonenumber (`recordOID`, `employeeOID`, `phonetype`, `phonenumber`) VALUES
  (1, 1, 'work', '12345'),
  (2, 2, 'work', '23456');