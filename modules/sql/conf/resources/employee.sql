CREATE TABLE IF NOT EXISTS Employee (
	employeeOID 	INT			 	PRIMARY KEY,
	firstname		VARCHAR(50) 	NOT NULL,
	lastname 		VARCHAR(50) 	NOT NULL,
	username		VARCHAR(20),
	chefOID			INT,
    FOREIGN KEY (chefOID) 			REFERENCES Employee(employeeOID)
);

CREATE TABLE IF NOT EXISTS Phonenumber (
	recordOID 		INT 			PRIMARY KEY,
	employeeOID 	INT,
	phonetype		ENUM('work', 'home', 'mobile'),
	phonenumber		VARCHAR(20)		NOT NULL,
    FOREIGN KEY (employeeOID) 		REFERENCES Employee(employeeOID)
);

CREATE TABLE IF NOT EXISTS Emailaddress (
	recordOID		INT				PRIMARY KEY,
	employeeOID		INT,
	emailtype		ENUM('work', 'private'),
	emailaddress	VARCHAR(50)		NOT NULL,
    FOREIGN KEY (employeeOID) 		REFERENCES Employee(employeeOID)
);

CREATE TABLE IF NOT EXISTS Jobtitle (
	titleOID		INT				PRIMARY KEY,
	name			VARCHAR(50)		NOT NULL,
	description		TEXT
);

CREATE TABLE IF NOT EXISTS Department (
	departmentOID	INT				PRIMARY KEY,
	name			VARCHAR(50)		NOT NULL
);

CREATE TABLE IF NOT EXISTS Position (
	employeeOID		INT,
	number			INT,
	salary			INT NOT NULL,
	begin			DATE NOT NULL,
	end				DATE,
	titleOID		INT,
	departmentOID	INT,
	PRIMARY KEY (employeeOID, number),
    FOREIGN KEY (employeeOID) 		REFERENCES Employee(employeeOID),
    FOREIGN KEY (titleOID)			REFERENCES Jobtitle(titleOID),
    FOREIGN KEY (departmentOID)		REFERENCES Department(departmentOID)
);

INSERT IGNORE INTO Employee (`employeeOID`, `firstname`, `lastname`, `username`, `chefOID`) VALUES
	(1, 'Max', 'Becker', 'max_becker', null),
	(2, 'Hannah', 'Müller', 'hanna_müller', null),
	(3, 'Lukas', 'Schuster', 'lukas_schuster', 1),
	(4, 'Ben', 'Schmidt', 'ben_schmidt', 2),
	(5, 'Lena', 'Fischer', 'lena_muster', 1),
	(6, 'Till', 'Koch', 'till_koch', 3),
	(7, 'Nina', 'Richter', 'nina_richter', 3),
	(8, 'Robert', 'Müller', 'robert_müller', 2);

INSERT IGNORE INTO Emailaddress (`recordOID`, `employeeOID`, `emailType`, `emailaddress`) VALUES
	(1, 1, 'work', 'max.becker@firma.com'),
	(2, 2, 'work', 'hannah.müller@firma.com');

INSERT IGNORE INTO Phonenumber (`recordOID`, `employeeOID`, `phonetype`, `phonenumber`) VALUES
	(1, 1, 'work', '12345'),
	(2, 2, 'work', '23456');