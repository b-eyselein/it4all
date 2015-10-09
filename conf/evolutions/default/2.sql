# --- !Ups

INSERT INTO Student (`name`) VALUES ('s319286'), ('s319287');

INSERT INTO Html_Exercise (`id`, `exercise_text`, `default_solution`) VALUES
  (1, 'Erstellen sie eine HTML-Seite mit folgenden Inhalten:',
  '<!DOCTYPE html>\n<html>\n<head>\n  <title>Titel</title>\n</head>\n<body>\n  Body...\n</body>\n</html>');
  
INSERT INTO Task (`exercise_id`, `task_Description`, `pts`) VALUES
  (1, 'Einem Paragraphen', 1);

# --- !Downs

DELETE FROM Student;

DELETE FROM Html_Exercise;

DELETE FROM Task;