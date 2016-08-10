# --- !Ups

# HTML

insert into exercise (`type`, `id`, `title`, `exerciseText`, `file_name`, `default_solution`, `function_name`) values
	("html", 1, "Terminabsprache Werkstatt", "Ihre Firma für Webdesign ist von einer lokalen Werkstatt beauftragt worden, ein Kontaktformular für die Absprache eines Termins zu erstellen. Die Kunden sollen in einem Formular ihre Daten (Namen, Adresse, Automarke, Baujahr, …) sowie einen Datumswunsch angeben. Diese Daten werden an den Server gesendet und, falls der Termin möglich ist, in einer Datenbank gespeichert. Falls der Termin bereits besetzt ist, bekommt der Kunde eine Fehlermeldung angezeigt. Die Werkstatt kann nur Modelle der Marken Audi, Seat, Skoda und VW reparieren.
	Ihre Firma beschließt, zuerst einen statischen Seitenprototyp mit verminderter Funktion zu erstellen. Benutzen Sie nur Html, um folgende Elemente zu erstellen. Versuchen Sie jedoch so viel Funktionalität wie möglich umzusetzen, indem Sie entsprechende Elemente bzw. Attribute von Html5 verwenden! Elemente, die mit einem „*“ markiert sind, sollen zur Formularabsendung eingegeben werden müssen. Benutzen Sie die in Klammern angegeben Namen für die Elemente! Erstellen Sie jedes Eingabefeld für das spätere Styling mit CSS jeweils eine eigene <div>-Umgebung!",
	"", "", ""),

	("js", 3, "Summen", "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.", "",
	 "function sum(a, b) {\n  return 0;;\n}", "sum"),
	 
	("js", 4, "Konkatenation von Strings", "Implementieren Sie die folgende Funktion 'concat', die drei beliebige Strings entgegennimmt
	 und die Konkatenation der Strings zurückgibt.", "",
	 "function concat(str1, str2, str3) {\n  return \"\";;\n}", "concat"),
	 
   	("spread", 5, "Planung Schullandheimaufenthalt", "Sie sind beauftragt, einen Schullandheimaufenthalt zu planen und die Kosten zu kalkulieren.", "Aufgabe_Schullandheim", "", "");
	
insert into xmlexercise (`id`, `title`, `exerciseType`, `referenceFileName`, `exerciseText`) values
	(1, "Hello, XML", "XMLAgainstDTD", "party.dtd", "Erstellen Sie zu dieser DTD ein passendes XML-Dokument"),
	(2, "Hello, XSD", "XMLAgainstXSD", "note.xsd", "Erstellen Sie zu diesem XML Schema ein passendes XML-Dokument"),
	(3, "Hello, DTD", "DTDAgainstXML", "party.xml", "Erstellen Sie für dieses XML-Dokument eine passende DTD");

insert into html_task (`exercise_id`, `task_id`, `taskDesc`, `xpath_query_name`, `attributes`, `defining_attribute`) values
	(1, 1, "Erstellen Sie ein Formular auf der Seite. Als Aktion soll test und als Methode 'post' angegeben werden.",
	"//form", "action=test;;method=post",  ""),

	(1, 2, "Geben Sie im Fomular eine <h1>-Überschrift mit passendem Text an", "//form//h1", "", ""),
	
	(1, 3, "Erstellen Sie ein Namensfeld* (name) für den kompletten Namen des Kunden.",
	"//form/div//input", "type=text;;required=true", "name=name"),
	
	(1, 4, "Erstellen Sie ein Feld für die Emailadresse* (email) des Kunden.",
	"//form/div//input", "type=email;;required=true", "name=email"),
	
	(1, 5, "Erstellen Sie ein Datumsfeld* (datum), um einen Wunschtermin angeben zu können. Die Vorgabe soll der 01.01.2016 sein.",
	"//form/div//input", "type=date;;required=true;;value=2016-01-01", "name=datum"),
	
	(1, 6, "Erstellen Sie ein Dropdownmenü* (marke), um eine der vier Automarken auswählen zu können.
	 Geben Sie außerdem als erste Option 'Bitte wählen' mit einem leeren 'value'-Attribut an,
	 damit Sie die 'required'-Option modellieren können.",
	"//form/div//select", "required=true", "name=marke"),
	 
	(1, 7, "Erstellen Sie ein Eingabefeld* (jahr) für das Baujahr des Autos (1950 <= year <= 2016). Als Vorgabe soll 2000 eingestellt sein.",
	"//form/div//input", "type=number;;required=true;;value=2000", "name=jahr"),
	 
	(1, 8, "Erstellen Sie eine Checkbox* (agb), um die AGBs der Seite zu akzeptieren",
	"//form/div//input", "type=checkbox;;required=true", "name=agb"),
	 
	(1, 9, "Erstellen Sie eine Möglichkeit, das Formular abzusenden. Geben Sie als Wert 'Absenden' an.",
	"//form/div//input", "value=Absenden", "type=submit");

insert into childtask (`id`, `task_id`, `exercise_id`, `tagName`, `defining_attribute`) values
	(1, 6, 1, "option", "value="),
 	(2, 6, 1, "option", "value=Audi"),
 	(3, 6, 1, "option", "value=Seat"),
 	(4, 6, 1, "option", "value=Skoda"),
 	(5, 6, 1, "option", "value=VW");
 	
insert into css_task (`task_id`, `exercise_id`, `taskdesc`, `xpath_query_name`, `defining_attribute`, `attributes`) values
	(1, 1, 'Binden Sie Bootstrap über folgenden Link ein: <link rel=\"stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">', 
	"//link", "rel=stylesheet", "href=http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"),
	(2, 1, "Geben Sie allen div-Elementen, die die einzelnen Inputs einschließen, die Klasse 'form-group'.", "//form/div", "", "class=form-group"),
	(3, 1, "Verwenden Sie für alle Inputs die Klasse form-control", "//form/div/input", "", "class=form-control");

	
# JavaScript

insert into js_test (`id`, `exercise_id`, `awaited_result`) values
	(1, 3, 2.0),
	(2, 3, 3.0),
	(3, 3, 87.0),
	(4, 3, 597.0),
	(5, 4, "Hallo Welt!"),
	(6, 4, "TestTestTest");

insert into js_testvalue (`id`, `test_id`, `value`) values
	(1, 1, 1), (2, 1, 1),
	(3, 2, 1), (4, 2, 2),
	(5, 3, 44), (6, 3, 43),
	(7, 4, "555.0"), (8, 4, "42.0"),
	(9, 5, '\'Hallo \''), (10, 5, "'Welt'"), (11, 5, "'!'"),
	(12, 6, "'Test'"), (13, 6, "'Test'"), (14, 6, "'Test'");


# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

delete from js_testvalue;

delete from js_test;

delete from childtask;

delete from css_task;

delete from html_task;

delete from xmlexercise;

delete from select_exercise;

delete from update_exercise;

delete from create_exercise;

delete from sql_scenario;

delete from exercise;

SET FOREIGN_KEY_CHECKS = 1;
