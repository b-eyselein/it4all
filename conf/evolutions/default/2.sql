# --- !Ups

INSERT INTO users (username, pw_hash, std_role, todo) VALUES
  ('bje40dc', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR'),
  ('jok30ni', '', 'RoleAdmin', 'AGGR'),
  ('alg81dm', '', 'RoleAdmin', 'AGGR'),
  ('s319787', '', 'RoleAdmin', 'AGGR'),
  ('s323295', '', 'RoleAdmin', 'AGGR'),
  ('developer', '$2a$10$SIC4CoGDP8DLZnoHiWDi2ePtUOrsBKBzUdxVGhxjEIg2gWcQ3PnkG', 'RoleSuperAdmin', 'AGGR')
ON DUPLICATE KEY UPDATE std_role = VALUES(std_role);

# INSERT INTO web_exercises (id, title, author, ex_text, ex_state, html_text, js_text) VALUES
#   (1, 'Listen in Html', 'bje40dc',
#    'Erstellen Sie eine Liste in HTML, die die die Autohersteller Audi, BMW und Mercedes-Benz und Volkswagen enthält. Achten Sie dabei für die Korrektur auf eine korrekte Schreibweise der einzelnen Hersteller!',
#    'ACCEPTED', '', ''),
#
#   (2, 'Tabellen in Html', 'bje40dc', 'Erstellen Sie eine Tabelle in HTML!', 'ACCEPTED', '', '');

# INSERT INTO html_tasks (task_id, exercise_id, text, xpath_query, attributes, text_content) VALUES
#   (1, 1,
#    'Binden Sie Bootstrap über einen Link ein. Die entsprechende Datei ist unter der URL "/assets/stylesheets/bootstrap.css" zu finden. Setzen Sie auch den entsprechenden Wert für das Attribut "rel"',
#    '/html/head/link', 'rel=stylesheet;;href=/assets/stylesheets/bootstrap.css', ''),
#   (2, 1, 'Erstellen Sie eine passende h1-Überschrift, die das Wort "Autohersteller" enthält.', '/html/body//h1', '',
#    'Autohersteller'),
#   (3, 1,
#    'Erstellen Sie eine ungeordnete Liste auf der Seite, die dann die einzelnen Hersteller enthalten wird. Geben Sie dieser Liste die Klasse \"list-group\".',
#    '/html/body//ul', 'class=list-group', ''),
#   (4, 1, 'Erstellen Sie das Listenelement für Audi. Geben Sie diesem Element die Klasse "list-group-item".',
#    '/html/body//ul/li[1]', 'class=list-group-item', 'Audi'),
#   (5, 1, 'Erstellen Sie das Listenelement für BMW. Geben Sie diesem Element die Klasse "list-group-item".',
#    '/html/body//ul/li[2]', 'class=list-group-item', 'BMW'),
#   (6, 1, 'Erstellen Sie das Listenelement für Mercedes. Geben Sie diesem Element die Klasse "list-group-item".',
#    '/html/body//ul/li[3]', 'class=list-group-item', 'Mercedes'),
#   (7, 1, 'Erstellen Sie das Listenelement für Volkswagen. Geben Sie diesem Element die Klasse "list-group-item".',
#    '/html/body//ul/li[4]', 'class=list-group-item', 'Volkswagen');

# INSERT INTO xml_exercises (id, title, author, ex_text, ex_state, exercise_type, root_node, ref_file_content) VALUES
#   (1, 'Party', 'bje40dc', 'Erstellen Sie zu dieser DTD ein passendes XML-Dokument.', 'ACCEPTED', 'XML_DTD', 'party',
#    'TODO'),
#   (2, 'Vorlesung', 'bje40dc', 'Erstellen Sie zu dieser DTD ein passendes XML-Dokument.', 'ACCEPTED', 'XML_DTD',
#    'lecture', 'TODO');

# --- !Downs

DELETE FROM xml_exercises;

DELETE FROM web_solutions;

DELETE FROM js_tasks;

DELETE FROM html_tasks;

DELETE FROM web_exercises;

DELETE FROM uml_exercises;

DELETE FROM spread_exercises;

DELETE FROM sql_exercises;

DELETE FROM sql_scenario;

DELETE FROM questions;

DELETE FROM quizzes;

DELETE FROM prog_exercises;

DELETE FROM ebnf_exercises;

DELETE FROM courses;

DELETE FROM users;
