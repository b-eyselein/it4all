# --- !Ups

INSERT INTO Student (`name`) VALUES ('s319286'), ('s319287');

INSERT INTO Html_Exercise (`id`, `exercise_name`, `exercise_text`, `default_solution`) VALUES
  (1, 'Formulare', 'Erstellen sie eine HTML-Seite mit folgenden Inhalten:',
  '<!DOCTYPE html>\n<html>\n<head>\n  <title>Titel</title>\n</head>\n<body>\n  Body...\n</body>\n</html>');
  
INSERT INTO Task (`id`, `exercise_id`, `task_Description`, `pts`) VALUES
  (1, 1, 'Einem Eingabefeld für einen Namen', 1),
  (2, 1, 'Einem Eingabefeld für eine Email', 1);

# --- !Downs

DELETE FROM Student;

DELETE FROM Html_Exercise;

DELETE FROM Task;