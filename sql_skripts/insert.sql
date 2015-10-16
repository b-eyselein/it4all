INSERT INTO student (`name`) VALUES ('default');

INSERT INTO exercise (`id`, `title`, `text`, `default_solution`) VALUES
  (1, 'Formulare', 'Sie sollen für eine Website ein Formular erstellen, mit dem sich neue Nutzer registrieren können. Dabei sollen verschiedene Dinge abgefragt werden. Nutzen Sie die in Klammern angegebenen Namen für die Inputfelder, um eine Korrektur zu ermöglichen.',
  '<!DOCTYPE html>\n<html>\n<head>\n  <title>Titel</title>\n</head>\n<body>\n  Body...\n</body>\n</html>');
  
INSERT INTO sub_exercise (`id`, `title`, `text`, `exercise_id`, `default_solution`) VALUES
  (1, 'Erstellung der Html-Seite', 'Text...', 1, ''),
  (2, 'Layout & Design mit CSS', 'Test...', 1, '');
  
INSERT INTO task (`id`, `exercise_id`, `sub_exercise_id`, `task_Description`, `pts`) VALUES
  (1, 1, 1, 'Einem Eingabefeld für einen Namen (name)', 1),
  (1, 2, 1, 'Einem Eingabefeld für eine Email (email)', 1);