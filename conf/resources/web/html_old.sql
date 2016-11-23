insert into html_exercise (`id`, `title`, `text`) values
	(1,
	"Terminabsprache Werkstatt",
	"Ihre Firma für Webdesign ist von einer lokalen Werkstatt beauftragt worden, ein Kontaktformular für die Absprache eines Termins zu erstellen. Die Kunden sollen in einem Formular ihre Daten (Namen, Adresse, Automarke, Baujahr, …) sowie einen Datumswunsch angeben. Diese Daten werden an den Server gesendet und, falls der Termin möglich ist, in einer Datenbank gespeichert. Falls der Termin bereits besetzt ist, bekommt der Kunde eine Fehlermeldung angezeigt. Die Werkstatt kann nur Modelle der Marken Audi, Seat, Skoda und VW reparieren.
	Ihre Firma beschließt, zuerst einen statischen Seitenprototyp mit verminderter Funktion zu erstellen. Benutzen Sie nur Html, um folgende Elemente zu erstellen. Versuchen Sie jedoch so viel Funktionalität wie möglich umzusetzen, indem Sie entsprechende Elemente bzw. Attribute von Html5 verwenden! Elemente, die mit einem „*“ markiert sind, sollen zur Formularabsendung eingegeben werden müssen. Benutzen Sie die in Klammern angegeben Namen für die Elemente! Erstellen Sie jedes Eingabefeld für das spätere Styling mit CSS jeweils eine eigene <div>-Umgebung!"
	),
	
	(3,
	"Listen",
	"Erstellen Sie eine Liste in HTML, die die die Autohersteller Audi, BMW und Mercedes-Benz und Volkswagen enthält. Achten Sie dabei für die Korrektur auf eine korrekte Schreibweise der einzelnen Hersteller!"
	);
	
	#("html",
	#2,
	#"Lebenlauf Autor",
	#"Erstellen Sie einen Steckbrief über das Leben des Autors Sir Arthur Conan Doyle. Dazu gehört eine Kurzbiographie und eine Übersicht über die berühmtesten Werke.");
	
insert into html_task (`exercise_id`, `task_id`, `taskDesc`, `xpath_query_name`, `attributes`, `defining_attribute`) values
	(1, 1, "Erstellen Sie ein Formular auf der Seite. Als Aktion soll test und als Methode 'post' angegeben werden. Alle anderen Elemente sollen sich in diesem Element befinden.",
	"//form", "action=test;;method=post",  ""),

	(1, 2, "Geben Sie im Fomular eine h1-Überschrift mit passendem Text an", "//form//h1", "", ""),
	
	(1, 3, "Erstellen Sie ein Namensfeld* (name) für den kompletten Namen des Kunden.",
	"//form//div//input[@name='name']", "type=text;;required=true", ""),
	
	(1, 4, "Erstellen Sie ein Feld für die Emailadresse* (email) des Kunden.",
	"//form//div//input[@type='email']", "name=email;;required=true", ""),
	
	(1, 5, "Erstellen Sie ein Datumsfeld* (datum), um einen Wunschtermin angeben zu können. Die Vorgabe soll der 01.01.2016 sein.",
	"//form//div//input[@type='date']", "name=datum;;required=true;;value=2016-01-01", ""),
	
	(1, 6, "Erstellen Sie ein Dropdownmenü* (marke), um eine der vier Automarken auswählen zu können.
	 Geben Sie außerdem als erste Option 'Bitte wählen' mit einem leeren 'value'-Attribut an,
	 damit Sie die 'required'-Option modellieren können.",
	"//form//div//select[@name='marke']", "required=true", ""),
	 
	(1, 7, "Erstellen Sie ein Eingabefeld* (jahr) für das Baujahr des Autos. Dies soll von 1950 bis einschließlich 2016 reichen. Als Vorgabe soll 2000 eingestellt sein.",
	"//form//div//input[@type='number']", "name=jahr;;required=true;;value=2000", ""),
	 
	(1, 8, "Erstellen Sie eine Checkbox* (agb), um die AGBs der Seite zu akzeptieren",
	"//form//div//input[@type='checkbox']", "name=agb;;required=true", ""),
	 
	(1, 9, "Erstellen Sie eine Möglichkeit, das Formular abzusenden. Geben Sie als Wert 'Absenden' an.",
	"//form//div//input[@type='submit']", "value=Absenden", ""),
	
	(3, 1, 'Erstellen Sie eine passende Überschrift (h1), die das Wort \"Autohersteller\" enthält.',
	'//h1[text()[contains(., \'Autohersteller\')]]', '' ,''),
	
	(3, 2, 'Erstellen Sie eine ungeordnete Liste auf der Seite, die dann die einzelnen Hersteller enthält',
	'//ul', '', ''),
	
 	(3, 3, 'Erstellen Sie das Listenelement für Audi',
 	'//ul/li[text()[contains(., \'Audi\')]]', '', ''),
 	
 	(3, 4, 'Erstellen Sie das Listenelement für BMW',
 	'//ul/li[text()[contains(., \'BMW\')]]', '', ''),
 	
 	(3, 5, 'Erstellen Sie das Listenelement für Mercedes-Benz',
 	'//ul/li[text()[contains(., \'Mercedes-Benz\')]]', '', ''),
 	
 	(3, 6, 'Erstellen Sie das Listenelement für Volkswagen',
 	'//ul/li[text()[contains(., \'Volkswagen\')]]', '', '');
	
	#(2, 1, 'Erstellen Sie eine <h1>-Überschrift für die Seite. Diese soll in ein <div>-Element eingebettet sein. Der Inhalt soll "Sir Arthur Conan Doyle" lauten.',
	#"//div/h1[text()[contains(., \'Sir Arthur Conan Doyle\')]]", "", ""),
	
	#(2, 2, 'Beschreibung',
	#"*", "", "");

insert into childtask (`id`, `task_id`, `exercise_id`, `tagName`, `defining_attribute`) values
	(1, 6, 1, "option", "value="),
 	(2, 6, 1, "option", "value=Audi"),
 	(3, 6, 1, "option", "value=Seat"),
 	(4, 6, 1, "option", "value=Skoda"),
 	(5, 6, 1, "option", "value=VW");
 	
insert into css_task (`task_id`, `exercise_id`, `taskdesc`, `xpath_query_name`, `defining_attribute`, `attributes`) values
	(1, 1, 'Binden Sie Bootstrap über folgenden Link ein: <link rel="stylesheet" href="/assets/stylesheets/bootstrap.css">', 
	'//link', '', 'rel=stylesheet;;href=/assets/stylesheets/bootstrap.css'),
	
	(2, 1, 'Geben Sie allen div-Elementen, die alle Inputs außer die Checkbox einschließen, die Klasse \'form-group\'.',
	'//form/div[not(descendant::*[@type = \'checkbox\'])]', '', 'class=form-group'),
	
	(3, 1, 'Geben Sie dem div-Element, das die Checkbox einschließt, die Klasse "checkbox".',
	'//form/div[descendant::*[@type = \'checkbox\']]', '', 'class=checkbox'),
	
	(4, 1, 'Verwenden Sie für alle Inputs außer der Checkbox für die AGB die Klasse "form-control".',
	'//form/div/input[not(contains(@type, \'checkbox\'))]', '', 'class=form-control'),
	
	(5, 1, 'Geben Sie dem Select-Element auch die Klass \'form-control\'.',
	'//select', '', 'class=form-control');
	
	