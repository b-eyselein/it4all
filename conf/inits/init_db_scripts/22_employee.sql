CREATE DATABASE employee;

USE employee;

GRANT ALL on employee.* TO it4all@'%';

-- Create all tables

CREATE TABLE IF NOT EXISTS employee (
  id        INT PRIMARY KEY,
  firstname VARCHAR(50) NOT NULL,
  lastname  VARCHAR(50) NOT NULL,
  username  VARCHAR(20),
  chef_id   INT,

  FOREIGN KEY (chef_id) REFERENCES employee (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

INSERT IGNORE INTO employee (`id`, `firstname`, `lastname`, `username`, `chef_id`) VALUES
  (1, 'Max', 'Becker', 'max_becker', NULL),
  (2, 'Hannah', 'Müller', 'hanna_mueller', NULL),
  (3, 'Lukas', 'Schuster', 'lukas_schuster', 1),
  (4, 'Ben', 'Schmidt', 'ben_schmidt', 2),
  (5, 'Lena', 'Fischer', 'lena_muster', 1),
  (6, 'Till', 'Koch', 'till_koch', 3),
  (7, 'Nina', 'Richter', 'nina_richter', 3),
  (8, 'Robert', 'Müller', 'robert_mueller', 2);

CREATE TABLE IF NOT EXISTS phonenumber (
  id          INT PRIMARY KEY,
  employee_id INT,
  phonetype   ENUM ('work', 'home', 'mobile'),
  phonenumber VARCHAR(20) NOT NULL,

  FOREIGN KEY (employee_id) REFERENCES employee (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

INSERT IGNORE INTO phonenumber (`id`, `employee_id`, `phonetype`, `phonenumber`) VALUES
  (1, 1, 'work', '12345'),
  (2, 2, 'work', '23456');

CREATE TABLE IF NOT EXISTS emailaddress (
  id           INT PRIMARY KEY,
  employee_id  INT,
  emailtype    ENUM ('work', 'private'),
  emailaddress VARCHAR(50) NOT NULL,

  FOREIGN KEY (employee_id) REFERENCES employee (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

INSERT IGNORE INTO emailaddress (`id`, `employee_id`, `emailtype`, `emailaddress`) VALUES
  (1, 1, 'work', 'max.becker@firma.com'),
  (2, 2, 'work', 'hannah.mueller@firma.com');

CREATE TABLE IF NOT EXISTS jobtitle (
  id          INT PRIMARY KEY,
  name        VARCHAR(50) NOT NULL,
  description TEXT
);

CREATE TABLE IF NOT EXISTS department (
  id   INT PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS position (
  employee_id   INT,
  number        INT,
  salary        INT  NOT NULL,
  begin         DATE NOT NULL,
  end           DATE,
  title_id      INT,
  department_id INT,

  PRIMARY KEY (employee_id, number),
  FOREIGN KEY (employee_id) REFERENCES employee (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (title_id) REFERENCES jobtitle (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (department_id) REFERENCES department (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);