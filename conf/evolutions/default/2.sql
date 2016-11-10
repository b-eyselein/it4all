# --- !Ups

# JavaScript
insert into js_web_exercise (`id`, `title`, `text`, `anterior`, `posterior`, `declaration`) values
	(1,
	'Klickzähler',
	'Implementieren Sie die folgende Funktion <code>count()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird. Sie soll den Text aus dem Element mit der id \"counter\" auslesen und um 1 erhöhen!',
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
	
insert into conditions (`id`, `pre_id`, `post_id`, `xpathquery`, `awaitedvalue`) values
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


delete from css_task;

delete from html_task;

delete from html_exercise;

SET FOREIGN_KEY_CHECKS = 1;
