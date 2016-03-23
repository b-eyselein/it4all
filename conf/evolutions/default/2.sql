# --- !Ups

insert into student values
	("s319286");
	
insert into htmlexercise values
	(1, "Terminabsprache Werkstatt", "TODO: Aufgabenstellung!");
	
insert into task (`dtype`, `exerciseid`, `id`, `taskDesc`, `tagName`, `attributes`, `elemName`, `title`) values
	("title", 1, 1, "Der Titel der Seite soll 'Kontaktformular Werkstatt' lauten.", "", "", "", "Kontaktformular Werkstatt"),
	
	("tag", 1, 2, "Erstellen Sie ein Formular auf der Seite. Als Aktion soll test und als Methode 'post' angegeben werden.",
	"form", "action=test;method=post", "", ""),
	
	("name", 1, 3, "Erstellen Sie ein Namensfeld* (name) für den kompletten Namen des Kunden", "input",
	"type=text;required=true", "name", ""),
	
	("name", 1, 4, "Erstellen Sie ein Feld für die Emailadresse* (email) des Kunden", "input", 
	"type=email;required=true", "email", ""),
	
	("name", 1, 5, "Erstellen Sie ein Datumsfeld* (datum), damit der Kunde seinen Wunschtermin angeben kann", "input",
	"type=date;required=true", "datum", "");
	
# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

delete from grading;

delete from task;

delete from htmlexercise;

delete from student;

SET FOREIGN_KEY_CHECKS = 1;