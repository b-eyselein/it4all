# --- !Ups

insert into student values
	("s319286");

# HTML
	
insert into htmlexercise values
	(1, "Terminabsprache Werkstatt", "Ihre Firma für Webdesign ist von einer lokalen Werkstatt beauftragt worden, ein Kontaktformular für die Absprache eines Termins zu erstellen. Die Kunden sollen in einem Formular ihre Daten (Namen, Adresse, Automarke, Baujahr, …) sowie einen Datumswunsch angeben. Diese Daten werden an den Server gesendet und, falls der Termin möglich ist, in einer Datenbank gespeichert. Falls der Termin bereits besetzt ist, bekommt der Kunde eine Fehlermeldung angezeigt.
	Die Werkstatt kann nur Modelle der Marken Audi, Seat, Skoda und VW reparieren.
	Ihre Firma beschließt, zuerst einen statischen Seitenprototyp mit verminderter Funktion zu erstellen. Benutzen Sie nur Html, um folgende Elemente zu erstellen. Versuchen Sie jedoch so viel Funktionalität wie möglich umzusetzen, indem Sie entsprechende Elemente bzw. Attribute von Html5 verwenden! Elemente, die mit einem „*“ markiert sind, sollen zur Formularabsendung eingegeben werden müssen.");
	
insert into task (`taskType`, `exercise_id`, `id`, `taskDesc`, `tagName`, `attributes`, `elemName`, `title`) values
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
	 Geben Sie außerdem als erste Option 'Bitte wählen' mit einem leeren 'value'-Attribut an,
	 damit Sie die 'required'-Option modellieren können.", "select", "required=true", "marke", ""),
	
	("name", 1, 7, "Ein weiteres Dropdownmenü (modell), um passende Automodelle auswählen zu können. Geben Sie je Hersteller
	 mindestens folgende Modell an: A3, Leon, Fabia, Golf.
	 Geben Sie außerdem als erste Option „Bitte wählen“ mit einem leeren „value“-Attribut an, damit Sie die „required“-Option
	 modellieren können.", "select", "required=true", "modell", ""),
	 
	("name", 1, 8, "Erstellen Sie ein Eingabefeld* (jahr) für das Baujahr des Autos (1950 <= year <= 2016). Als Vorgabe soll
	 2000 eingestellt sein.",
	 "input", "type=number:required=true:value=2000", "jahr", ""),
	 
	("name", 1, 9, "Erstellen Sie eine Checkbox* (agb), um die AGBs der Seite zu akzeptieren",
	 "input", "type=checkbox:required=true", "agb", ""),
	 
	("tag", 1, 10, "Erstellen Sie eine Möglichkeit, das Formular abzusenden. Geben Sie als Wert 'Absenden' an.",
	 "input", "type=submit:value=Absenden", "", "");

insert into childtask (`id`, `task_id`, `exercise_id`, `tagName`, `attributes`) values
	(1, 6, 1, "option", "value="),
 	(2, 6, 1, "option", "value=Audi"),
 	(3, 6, 1, "option", "value=Seat"),
 	(4, 6, 1, "option", "value=Skoda"),
 	(5, 6, 1, "option", "value=VW"),
 	(1, 7, 1, "option", "value=A3"),
 	(2, 7, 1, "option", "value=Leon"),
 	(3, 7, 1, "option", "value=Fabia"),
 	(4, 7, 1, "option", "value=Golf");

 
# JavaScript
 
insert into js_exercise (`id`, `name`, `text`, `default_solution`, `function_name`) values
	(1, "Summen", "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.",
	 "function sum(a, b) {\n  return 0\n}", "sum"),
	(2, "Konkatenation von Strings", "Implementieren Sie die folgende Funktion 'concat',
	 die drei beliebige Strings entgegennimmt und die Konkatenation der Strings zurückgibt.",
	 "function concat(str1, str2, str3) {\n  return \"\"\n}", "concat");

insert into js_test (`id`, `exercise_id`, `awaited_result`) values
	(1, 1, 2),
	(2, 1, 3),
	(3, 1, 87),
	(4, 2, "Hallo Welt!"),
	(5, 2, "TestTest");

insert into js_testvalue (`id`, `test_id`, `value`) values
	(1, 1, 1), (2, 1, 1),
	(3, 2, 1), (4, 2, 2),
	(5, 3, 44), (6, 3, 43),
	(7, 4, "'Hallo '"), (8, 4, "'Welt'"), (9, 4, "'!'"),
	(10, 5, "'Test'"), (11, 5, "'Test'");
	
# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

delete from grading;

delete from js_testvalue;

delete from js_test;

delete from js_exercise;

delete from childtask;

delete from task;

delete from htmlexercise;

delete from student;

SET FOREIGN_KEY_CHECKS = 1;