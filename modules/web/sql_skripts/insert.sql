-- HTML
INSERT INTO html_exercise (`id`, `title`, `text`) VALUES
  (1, "Formulare",
  "Sie sind beauftragt, für die Internetseite einer Werkstatt ein Kontaktformular für die Terminabsprache erstellen.
  Das Formular (form) soll folgenden Umfang haben:"),
  (2, "Terminabsprache Werkstatt", "TODO");
  
INSERT INTO task (`exercise_id`, `id`, `task_Description`, `pts`, `result_type`, `tag_name`, `element_name`, `attributes`) VALUES
  (1, 1, 'Es soll als Methode „post“ und als Aktion „test“ verwendet werden', 2, 0, 'form', 'form', 'method=post;action=test'),
  (1, 2, 'Ein Namensfeld* (name)', 2, 0, 'input', 'name', 'type=text;required=true'),
  (1, 3, 'Ein Feld für Emailadressen* (email)', 2, 0, 'input', 'email', 'type=email;required=true'),
  (1, 4, 'Drei Radiobuttons (zeit), um die gewünschte Tageszeit für den Termin anzugeben. Die Werte sollen morgens, mittags und nachmittags sein.',
  			2, 2, 'input', 'zeit', '#value=morgens;value=mittags;value=nachmittags;'),
  (1, 5, 'Ein Datumsfeld* (datum), um den gewünschten Termin anzugeben. Die Werte sollen morgens, mittags und nachmittags sein.', 2, 0,
				'input', 'datum', 'type=date;required=true'),
  (1, 6, 'Eine Checkbox* (agb), um die AGBs der Seite zu akzeptieren', 2, 0, 'input', 'agb', 'type=checkbox;required=true'),
  (1, 7, 'Eine Möglichkeit, das Formular zurückzusetzen (reset)', 1, 0, 'input', 'reset', 'type=reset'),
  (1, 8, 'Eine Möglichkeit, das Formular abzusenden (submit)', 1, 0, 'input', 'submit', 'type=submit'),
  
  (2, 1, 'Erstellen Sie ein Formular, in dem die Daten abgefragt werden. Es soll als Methode "post" und als Aktion "test" verwendet werden.',
				2, 1, 'form', '', 'method=post;action=test'),
  (2, 2, 'Ein Namensfeld* (name) für den (kompletten) Namen des Kunden', 2, 0, 'input', 'name', 'type=text;required=true'),
  (2, 3, 'Ein Datumsfeld* (datum), um einen Wunschtermin anzugeben', 2, 0, 'input', 'datum', 'type=date;required=true'),
  (2, 5, 'Ein Eingabefeld* (jahr), um das Baujahr des Autos anzugeben (1950 <= year <= 2016)', 2, 0, 'input', 'jahr', 'type=number;min=1950;max=2016');

-- JavaScript
INSERT INTO js_exercise (`id`, `name`, `text`, `default_solution`, `function_name`) VALUES
	(1, "Summen", "Implementieren Sie folgende Funktion 'sum', die zwei Zahlen entegennimmt und deren Summe zurückgibt.", "function sum(a, b) {\n  return 0;\n}", "sum"),
	(2, "Konkatenation von Strings", "Implementieren Sie die folgende Funktion 'concat', die drei beliebige Strings entgegennimmt und die Konkatenation der Strings zurückgibt.", "function concat(str1, str2, str3) {\n  return \"\";\n}", "concat");

INSERT INTO js_test (`id`, `exercise_id`, `awaited_result`) VALUES
	(1, 1, 2),
	(2, 1, 3),
	(3, 1, 87),
	(4, 2, "Hallo Welt!");

INSERT INTO js_testvalue (`id`, `test_id`, `value`) VALUES
	(1, 1, 1), (2, 1, 1),
	(3, 2, 1), (4, 2, 2),
	(5, 3, 44), (6, 3, 43),
	(7, 4, "'Hallo '"), (8, 4, "'Welt'"), (9, 4, "'!'");
	