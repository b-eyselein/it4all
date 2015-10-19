INSERT INTO exercise (`id`, `title`, `text`, `default_solution`) VALUES
  (1, 'Formulare', 'Erstellen Sie für eine Webseite ein Formular zur Registrierung eines neuen Nutzers. Benutzen Sie 
jeweils die in Klammern angegebenen Namen für die Elemente, um eine Korrektur zu ermöglichen, 
und die in der Vorlesung gezeigten Eingabetypen. Elemente, die mit einem * gekennzeichnet sind, 
sollen dabei im Formuar zwingend eingegeben werden müssen.',
  '<!DOCTYPE html>\n<html>\n<head>\n  <title>Titel</title>\n</head>\n<body>\n  Body...\n</body>\n</html>');
  
INSERT INTO sub_exercise (`id`, `title`, `text`, `exercise_id`, `default_solution`) VALUES
  (1, 'Erstellung der Html-Seite', 'Erstellen Sie das Formular. Das Formular soll folgende Eigenschaften besitzen:', 1, '');
  
INSERT INTO task (`id`, `exercise_id`, `sub_exercise_id`, `task_Description`, `pts`) VALUES
  (1, 1, 1, 'Benutzen Sie für das Formular die Post-Methode und geben Sie als Action „test“ an.', 2),
  (2, 1, 1, 'Die Nutzer sollen ihre Namen* („name“) angeben können', 2),
  (3, 1, 1, 'Die Nutzer müssen ihre Emailadresse* („email“)', 2),
  (4, 1, 1, 'Sie sollen ein Passwort* („password“) angeben können', 2),
  (5, 1, 1, 'Aufgrund gesetzlicher Bestimmungen soll es außerdem möglich sein, die AGBs* („agb“) der Seite zu akzeptieren.', 2),
  (6, 1, 1, 'Fügen Sie außerdem Möglichkeiten ein, das Formular zurückzusetzen (reset)', 1),
  (7, 1, 1, 'beziehungsweise abzusenden (submit)', 1);