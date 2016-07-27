# SQL

insert into sql_scenario(`short_name`, `long_name`) values
	('phone', 'Telefon- und Emailverzeichnis');

insert into sql_exercise (`scenario_name`, `id`, `title`, `text`, `ex_type`) values
	('phone', 1, 'Create todo', 'Erstellen Sie das CREATE-Statement für die Tabelle users!', 'CREATE'),
	('phone', 2, 'Alle Telefonnumern', 'Geben Sie alle Telefonnummern aus!', 'SELECT'),
	('phone', 3, 'Wer hat ein Geschäftshandy?', 'Geben Sie die Vor- und Nachnamen aller Personen aus, die eine Geschäftsnummer besitzen', 'SELECT'),
	('phone', 4, 'Handy verloren...', 'Martina Musterfrau (Nutzername f_martina) hat ein neues Handy mit der Nummer 2345 bekommen. Aktualisieren Sie den Eintrag!', 'UPDATE');

insert into sql_sample_solution (`sample_id`, `exercise_id`, `scenario_name`, `sample`) values
	(1, 1, 'phone', "CREATE TABLE users (\n\tusername varchar(20) primary key,\n\tfirstname varchar(50),\n\tlastname varchar(50)\n);;"),
	(1, 2, 'phone', "SELECT phonenumber\n\tFROM phone;;"),
	(1, 3, 'phone', "SELECT firstname, lastname\n\tFROM phone\n\tJOIN users ON phone.username = users.username\n\tWHERE phonetype = \'work\';;"),
	(2, 3, 'phone', "SELECT lastname, firstname\n\tFROM phone p, users u\n\tWHERE u.username = p.username\n\tAND phonetype = \'work\';;"),
	(1, 4, 'phone', "UPDATE phone\n\tSET phonenumber = \'2345\'\n\tWHERE username = \'f_martina\';;");
