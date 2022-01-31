package initialData.sql

import initialData.InitialExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}
import model.{Level, TopicWithLevel}

object SqlColl1Exes01To10 {

  private val schemaName = "employee"

  val sql_coll_1_ex_1: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Anzahl der Angestellten",
    authors = Seq("bje40dc"),
    text = """Wie viele Angestellte hat die Firma? Benennen Sie das Ergebnis als 'Anzahl'.""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Beginner),
      TopicWithLevel(SqlTopics.Alias, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        """SELECT COUNT(*) AS Anzahl
          |    FROM employee;""".stripMargin,
        """SELECT COUNT(id) AS Anzahl
          |    FROM employee;""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_2: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Angestelltennummer",
    authors = Seq("bje40dc"),
    text = """Welche Angestelltennummer (id) hat Max Becker?""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        """SELECT id
          |    FROM employee
          |    WHERE firstname = 'Max' AND lastname = 'Becker';""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_3: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Emailadresse",
    authors = Seq("bje40dc"),
    text = "Welche Emailadresse hat Max Becker für die Arbeit?",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Join, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        """SELECT emailaddress
          |    FROM employee JOIN emailaddress ON employee.id = emailaddress.employee_id
          |    WHERE firstname = 'Max' AND lastname = 'Becker';""".stripMargin,
        """SELECT emailaddress
          |    FROM employee JOIN emailaddress ON employee.id = employee_id
          |    WHERE firstname = 'Max' AND lastname = 'Becker';""".stripMargin,
        """SELECT emailaddress
          |    FROM employee, emailaddress
          |    WHERE employee.id = emailaddress.employee_id AND firstname = 'Max' AND lastname = 'Becker';""".stripMargin,
        """SELECT emailaddress
          |    FROM employee as emp, emailaddress as email
          |    WHERE emp.id = email.employee_id AND firstname = 'Max' AND lastname = 'Becker';""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_4: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Nutzernamen",
    authors = Seq("bje40dc"),
    text = "Geben Sie die Nutzernamen aller Angestellten alphabetisch geordnet aus!",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.OrderBy, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        """SELECT username
          |    FROM employee
          |    ORDER BY username;""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_5: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Anzahl der Untergebenen",
    authors = Seq("bje40dc"),
    text = """Wie viele Untergebene hat jeder Chef? Geben Sie jeweils die OID des Chefs und die Anzahl aus!""",
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.GroupBy, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        """SELECT chef_id, count(id)
          |    FROM employee
          |    WHERE chef_id IS NOT NULL
          |    GROUP BY chef_id;""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_6: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Tabelle 'employee' erstellen""",
    authors = Seq("bje40dc"),
    text = "Erstellen Sie das CREATE TABLE-Skript für die Tabelle employee!",
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Create, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.CREATE,
      schemaName,
      sampleSolutions = Seq(
        """CREATE TABLE employee (
          |    id INT PRIMARY KEY,
          |    firstname VARCHAR(50),
          |    lastname VARCHAR(50),
          |    username VARCHAR(20),
          |    chef_id INT,
          |    FOREIGN KEY (chef_id) REFERENCES employee(id)
          |);""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_7: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Versetzung",
    authors = Seq("bje40dc"),
    text = """Der Angestellte mit der OID 8 arbeitet jetzt für den Angestellten mit der OID 3.
             |Aktualisieren Sie die Datenbank!""".stripMargin
      .replace("\n", " "),
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Update, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.UPDATE,
      schemaName,
      sampleSolutions = Seq(
        """UPDATE employee
          |    SET chef_id = 3
          |    WHERE id = 8;""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_8: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Kündigung",
    authors = Seq("bje40dc"),
    text = """Der Angestellte mit der OID 7 hat gekündigt. Löschen Sie ihn aus der Angestelltentabelle.""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Delete, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName,
      sampleSolutions = Seq(
        """DELETE FROM employee
          |    WHERE id = 7;""".stripMargin
      )
    )
  )

  val sql_coll_1_ex_9: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Neue Kollegin",
    authors = Seq("bje40dc"),
    text = """Es gibt eine neue Angestellte mit Namen Tina Sattler.
             |Diese arbeitet für die Person mit der OID 2 und soll als OID 9 und als Nutzernamen 'tina_sattler' bekommen.""".stripMargin,
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Insert, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName,
      sampleSolutions = Seq(
        """INSERT INTO employee VALUES (9, 'Tina', 'Sattler', 'tina_sattler', 2);""",
        """INSERT INTO employee (id, firstname, lastname, username, chef_id)
          |VALUES (9, 'Tina', 'Sattler', 'tina_sattler', 2);""".stripMargin
      )
    )
  )

}
