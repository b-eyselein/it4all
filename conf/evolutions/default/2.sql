# --- !Ups

insert into users values
	("admin", "ADMIN", "SHOW")
	on duplicate key update role = values(role);

# JavaScript
insert into js_web_exercise (`id`, `title`, `text`, `declaration`) values
	(1,
	'Klickzähler',
	'Implementieren Sie die Funktion <code>count()</code>, die aufgerufen wird, wenn auf den Knopf gedrückt wird. Sie soll den Inhalt (innerHTML) des Elementes mit der id \"counter\" auslesen und um 1 erhöhen. Sie können die Funktion <code>parseInt(str)</code> verwenden, um einen String in eine Ganzzahl umzuwandeln.',
	'<!DOCTYPE html>\n<html>\n<head>\n  <title>Aufgabe</title>\n  <script type=\"text/javascript\">\n  function count() {\n  \n  }\n  </script>\n</head>\n\n<body>\n  <h3>Erhöhen Sie den Counter um 1, wenn der Button gedrückt wird!</h3>\n  <input type=\"button\" onclick=\"count()\" value=\"Push me!\">\n  <p>Count: <span id=\"counter\">0</span></p>\n</body>\n</html>'),
	
	(2,
	'Binärzahlen',
	'Implementieren Sie die Funktion <code>toBinary(number)</code>, die die übergebene Zahl in eine Binärzahl umwandelt und in das Ausgabefeld (span mit der id \"result\") schreibt!',
	'<!DOCTYPE html>\n<html>\n<head>\n  <title>Binärzahlen</title>\n  <script type=\"text/javascript\">\n  function toBinary(number) {\n  \n  }\n  </script>\n</head>\n\n<body>\n  <h3>Geben Sie eine Zahl ein:</h3>\n  <p>Ihre Zahl: <input type=\"number\" id=\"theInput\" \n\t\tonchange=\"toBinary(parseInt(this.value))\"></p>\n  <p>Binärzahl: <span id=\"result\"></span></p>\n</body>\n</html>');

insert into js_web_test (`id`, `exercise_id`,  `actiontype`, `xpath_query`, `keys_to_send`) values
	(1, 1, "CLICK", "//input[@type='button']", ""),
	(2, 1, "CLICK",  "//input[@type='button']", ""),
	(3, 1, "CLICK",  "//input[@type='button']", ""),
	(4, 1, "CLICK",  "//input[@type='button']", ""),
	(5, 1, "CLICK",  "//input[@type='button']", ""),
	(6, 2, "FILLOUT", "//input[@id='theInput']", "1"),
	(7, 2, "FILLOUT", "//input[@id='theInput']", "2"),
	(8, 2, "FILLOUT", "//input[@id='theInput']", "7");
	
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

SET FOREIGN_KEY_CHECKS = 1;
