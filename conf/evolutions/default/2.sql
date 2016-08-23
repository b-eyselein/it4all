# --- !Ups

# HTML

insert into exercise (`type`, `id`, `title`, `exerciseText`) values
	("html",
	1,
	"Terminabsprache Werkstatt",
	"Ihre Firma für Webdesign ist von einer lokalen Werkstatt beauftragt worden, ein Kontaktformular für die Absprache eines Termins zu erstellen. Die Kunden sollen in einem Formular ihre Daten (Namen, Adresse, Automarke, Baujahr, …) sowie einen Datumswunsch angeben. Diese Daten werden an den Server gesendet und, falls der Termin möglich ist, in einer Datenbank gespeichert. Falls der Termin bereits besetzt ist, bekommt der Kunde eine Fehlermeldung angezeigt. Die Werkstatt kann nur Modelle der Marken Audi, Seat, Skoda und VW reparieren.
	Ihre Firma beschließt, zuerst einen statischen Seitenprototyp mit verminderter Funktion zu erstellen. Benutzen Sie nur Html, um folgende Elemente zu erstellen. Versuchen Sie jedoch so viel Funktionalität wie möglich umzusetzen, indem Sie entsprechende Elemente bzw. Attribute von Html5 verwenden! Elemente, die mit einem „*“ markiert sind, sollen zur Formularabsendung eingegeben werden müssen. Benutzen Sie die in Klammern angegeben Namen für die Elemente! Erstellen Sie jedes Eingabefeld für das spätere Styling mit CSS jeweils eine eigene <div>-Umgebung!"
	),
	
	("html",
	2,
	"Lebenlauf Autor",
	"Erstellen Sie einen Steckbrief über das Leben des Autors Sir Arthur Conan Doyle. Dazu gehört eine Kurzbiographie und eine Übersicht über die berühmtesten Werke.");
	
insert into html_task (`exercise_id`, `task_id`, `taskDesc`, `xpath_query_name`, `attributes`, `defining_attribute`) values
	(1, 1, "Erstellen Sie ein Formular auf der Seite. Als Aktion soll test und als Methode 'post' angegeben werden.",
	"//form", "action=test;;method=post",  ""),

	(1, 2, "Geben Sie im Fomular eine <h1>-Überschrift mit passendem Text an", "//form//h1", "", ""),
	
	(1, 3, "Erstellen Sie ein Namensfeld* (name) für den kompletten Namen des Kunden.",
	"//form/div//input[@name='name']", "type=text;;required=true", ""),
	
	(1, 4, "Erstellen Sie ein Feld für die Emailadresse* (email) des Kunden.",
	"//form/div//input[@name='email']", "type=email;;required=true", ""),
	
	(1, 5, "Erstellen Sie ein Datumsfeld* (datum), um einen Wunschtermin angeben zu können. Die Vorgabe soll der 01.01.2016 sein.",
	"//form/div//input[@name='datum']", "type=date;;required=true;;value=2016-01-01", ""),
	
	(1, 6, "Erstellen Sie ein Dropdownmenü* (marke), um eine der vier Automarken auswählen zu können.
	 Geben Sie außerdem als erste Option 'Bitte wählen' mit einem leeren 'value'-Attribut an,
	 damit Sie die 'required'-Option modellieren können.",
	"//form/div//select[@name='marke']", "required=true", ""),
	 
	(1, 7, "Erstellen Sie ein Eingabefeld* (jahr) für das Baujahr des Autos (1950 <= year <= 2016). Als Vorgabe soll 2000 eingestellt sein.",
	"//form/div//input[@name='jahr']", "type=number;;required=true;;value=2000", ""),
	 
	(1, 8, "Erstellen Sie eine Checkbox* (agb), um die AGBs der Seite zu akzeptieren",
	"//form/div//input[@name='agb']", "type=checkbox;;required=true", ""),
	 
	(1, 9, "Erstellen Sie eine Möglichkeit, das Formular abzusenden. Geben Sie als Wert 'Absenden' an.",
	"//form/div//input[@type='submit']", "value=Absenden", ""),
	
	(2, 1, 'Erstellen Sie eine <h1>-Überschrift für die Seite. Diese soll in ein <div>-Element eingebettet sein. Der Inhalt soll "Sir Arthur Conan Doyle" lauten.',
	"//div/h1[text()[contains(., \'Sir Arthur Conan Doyle\')]]", "", ""),
	
	(2, 2, 'Beschreibung',
	"*", "", "");

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
	(3, 1, "Verwenden Sie für alle Inputs außer der Checkbox für die AGB die Klasse form-control", "//form/div/input[not(contains(@type, 'checkbox'))]", "", "class=form-control");

	
# JavaScript

insert into js_exercise (`id`, `title`, `text`, `declaration`, `function_name`) values
	(1, "Summen", "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.", 
	 "function sum(a, b) {\n  return 0;;\n}", "sum"),
	 
	(2, "Konkatenation von Strings", "Implementieren Sie die folgende Funktion 'concat', die drei beliebige Strings entgegennimmt
	 und die Konkatenation der Strings zurückgibt.", 
	 "function concat(str1, str2, str3) {\n  return \"\";;\n}", "concat");

insert into js_test (`id`, `exercise_id`, `awaited_result`) values
	(1, 1, 2.0),
	(2, 1, 3.0),
	(3, 1, 87.0),
	(4, 1, 597.0),
	(5, 2, "Hallo Welt!"),
	(6, 2, "TestTestTest");

insert into js_testvalue (`id`, `test_id`, `value`) values
	(1, 1, 1), (2, 1, 1),
	(3, 2, 1), (4, 2, 2),
	(5, 3, 44), (6, 3, 43),
	(7, 4, "555.0"), (8, 4, "42.0"),
	(9, 5, '\'Hallo \''), (10, 5, "'Welt'"), (11, 5, "'!'"),
	(12, 6, "'Test'"), (13, 6, "'Test'"), (14, 6, "'Test'");

insert into js_web_exercise (`id`, `title`, `text`, `anterior`, `posterior`, `declaration` ) values
	(1,
	'Klickzähler',
	'Implementieren Sie die folgende Funktion <code>count()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird. Sie soll den folgenden Text mit der id \"theText\" beim ersten Drücken ausblenden und beim erneuten Drücken wieder einblenden!',
	'<!DOCTYPE html>\n<html>\n<head>\n  <title>Aufgabe</title>\n  <script type=\"text/javascript\">',
	'  </script>\n</head>\n\n<body>\n  <h3>Erhöhen Sie den Counter um 1, wenn der Button gedrückt wird!</h3>\n  <input type=\"button\" onclick=\"count()\" value=\"Push me!\">\n  <p>Count: <span id=\"counter\">0</span></p>\n</body>\n<html>',
	'function count() {\n  \n}'),
	
	(2,
	'Binärzahlen',
	'Implementieren Sie die folgende Funktion <code>toBinary(number)</code>, die die übergebene Zahl in eine Binärzahl umwandelt und in das Ausgabefeld (span mit der id \"result\") schreibt!',
	'<!DOCTYPE html>\n<html>\n<head>\n  <title>Binärzahlen</title>\n  <script type=\"text/javascript\">',
	'  </script>\n</head>\n\n<body>\n  <h3>Geben Sie eine Zahl ein:</h3>\n  <p>Ihre Zahl: <input type=\"number\" id=\"theInput\" \n\t\tonchange=\"toBinary(parseInt(this.value))\"></p>\n  <p>Binärzahl: <span id=\"result\"></span></p>\n</body>\n<\html>',
	'function toBinary(number) {\n  \n}'),
	
	(3,
	'Vorspeisenwahl',
	'Implementieren Sie die folgende Funktion <code>disableSelection(wantsAppetizer)</code>, die einen booleschen Wert übergeben bekomment, je nachdem, ob "Ja" (wahr) oder "Nein" (ausgewählt) wurde. Wenn keine Vorspeise gewünscht ist, soll das Auswahlfeld deaktiviert, die Wahl auf das erste Element ("Bitte wählen...") gesetzt werden und eine Eingabe nicht mehr verpflichtend sein. Wenn eine Vorspeise gewünscht ist, muss die Auswahl aktiviert werden und verpflichtend sein. Der Abschicken-Button ist nur der Vollständigkeit halber vorhanden und soll nicht geändert werden.',
	'<!DOCTYPE html>\n<html>\n<head>\n  <title>Vorspeisenwahl</title>\n  <script type="text/javascript">',
	'  </script>\n</head>\n\n<body>\n  <h3>Wollen Sie eine Vorspeise?</h3>\n  <form action=\"test\" method=\"post\">\n    <p><label for=\"yes\"><input type=\"radio\" id=\"yes\"\n      name=\"appetizer\" onclick=\"disableSelection(true)\" required>Ja\n    </label></p>\n    <p><label for=\"no\"><input type=\"radio\" id=\"no\"\n      name=\"appetizer\" onclick=\"disableSelection(false)\" required>Nein\n    </label></p>\n    <p><select id=\"appetizerchoice\" required>\n      <option value=\"\">Bitte w&auml;;hlen</option>\n      <option>Gemischter Salat</option>\n      <option>Tomate und Mozarella</option>\n      <option>Tomatensuppe</option>\n      <option>Spargelsuppe</option>\n    </select></p>\n    <p><input type=\"submit\" value=\"Wahl abschicken\" disabled></p>\n  </form>\n</body>\n</html>',
	'function disableSelection(wantsAppetizer) {\n  \n}');

insert into js_web_test (`id`, `exercise_id`,  `actiontype`, `xpath_query`, `keys_to_send`) values
	(1, 1, "CLICK", "//input[@type='button']", ""),
	(2, 1, "CLICK",  "//input[@type='button']", ""),
	(3, 1, "CLICK",  "//input[@type='button']", ""),
	(4, 1, "CLICK",  "//input[@type='button']", ""),
	(5, 1, "CLICK",  "//input[@type='button']", ""),
	(6, 2, "FILLOUT", "//input[@id='theInput']", "1"),
	(7, 2, "FILLOUT", "//input[@id='theInput']", "2"),
	(8, 2, "FILLOUT", "//input[@id='theInput']", "7"),
	(9, 3, "CLICK", "//input[@id='yes']", "");
	
insert into conditions (`id`, `pre_id`, `post_id`, `xpath_query`, `to_evaluate`) values
	(1, 1, NULL,  "//span[@id='counter']", "0"),
	(2, 2, 1, "//span[@id='counter']", "1"),
	(3, 3, 2, "//span[@id='counter']", "2"),
	(4, 4, 3, "//span[@id='counter']", "3"),
	(5, 5, 4, "//span[@id='counter']", "4"),
	(6, NULL, 5, "//span[@id='counter']", "5"),
	(7, NULL, 6, "//span[@id='result']", "1"),
	(8, NULL, 7, "//span[@id='result']", "1100"),
	(9, NULL, 8, "//span[@id='result']", "1111111");

# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;

delete from js_web_test;

delete from conditions;

delete from js_web_exercise;

delete from js_testvalue;

delete from js_test;

delete from js_exercise;

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
