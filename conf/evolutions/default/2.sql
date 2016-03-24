# --- !Ups

insert into student values
	("s319286");
	
insert into htmlexercise values
	(1, "Terminabsprache Werkstatt", "Ihre Firma für Webdesign ist von einer lokalen Werkstatt beauftragt worden, ein Kontaktformular für die Absprache eines Termins zu erstellen. Die Kunden sollen in einem Formular ihre Daten (Namen, Adresse, Automarke, Baujahr, …) sowie einen Datumswunsch angeben. Diese Daten werden an den Server gesendet und, falls der Termin möglich ist, in einer Datenbank gespeichert. Falls der Termin bereits besetzt ist, bekommt der Kunde eine Fehlermeldung angezeigt.
	Die Werkstatt kann nur Modelle der Marken Audi, Seat, Skoda und VW reparieren.
	Ihre Firma beschließt, zuerst einen statischen Seitenprototyp mit verminderter Funktion zu erstellen. Benutzen Sie nur Html, um folgende Elemente zu erstellen. Versuchen Sie jedoch so viel Funktionalität wie möglich umzusetzen, indem Sie entsprechende Elemente bzw. Attribute von Html5 verwenden! Elemente, die mit einem „*“ markiert sind, sollen zur Formularabsendung eingegeben werden müssen.");
	
insert into task (`dtype`, `exercise_id`, `id`, `taskDesc`, `tagName`, `attributes`, `elemName`, `title`) values
	("title", 1, 1, "Der Titel der Seite soll 'Kontaktformular Werkstatt' lauten.", "", "", "", "Kontaktformular Werkstatt"),
	
	("tag", 1, 2, "Erstellen Sie ein Formular auf der Seite. Als Aktion soll test und als Methode 'post' angegeben werden.",
	"form", "action=test:method=post", "", ""),
	
	("name", 1, 3, "Erstellen Sie ein Namensfeld* (name) für den kompletten Namen des Kunden.", "input",
	"type=text:required=true", "name", ""),
	
	("name", 1, 4, "Erstellen Sie ein Feld für die Emailadresse* (email) des Kunden.", "input", 
	"type=email:required=true", "email", ""),
	
	("name", 1, 5, "Erstellen Sie ein Datumsfeld* (datum), um einen Wunschtermin angeben zu können.
	Die Vorgabe soll der 01.01.2016 sein.", "input",
	"type=date:required=true:value=2016-01-01", "datum", ""),
	
	("name", 1, 6, "Erstellen Sie ein Dropdownmenü* (marke), um eine der vier Automarken auswählen zu können.
	Geben Sie außerdem als erste Option „Bitte wählen“ mit einem leeren „value“-Attribut an,
	damit Sie die „required“-Option modellieren können.", "select", "", "marke", "");

insert into childtask (`id`, `task_id`, `exercise_id`, `tagName`, `attributes`) values
	(1, 6, 1, "option", "value="),
	(2, 6, 1, "option", "value=Audi"),
	(3, 6, 1, "option", "value=Seat"),
	(4, 6, 1, "option", "value=Skoda"),
	(5, 6, 1, "option", "value=VW");

# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

delete from grading;

delete from task;

delete from htmlexercise;

delete from student;

SET FOREIGN_KEY_CHECKS = 1;