package initialData.sql

import model.tools.sql.SqlTool.SqlExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}
import model.{Exercise, Level, SampleSolution, TopicWithLevel}

object SqlColl1Exes01To10 {

  private val schemaName = "employee"

  private val sql_coll_1_ex_1: SqlExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """SELECT COUNT(*) AS Anzahl
                     |    FROM employee;""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """SELECT COUNT(id) AS Anzahl
                     |    FROM employee;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_2: SqlExercise = Exercise(
    exerciseId = 2,
    collectionId = 1,
    toolId = "sql",
    title = "Angestelltennummer",
    authors = Seq("bje40dc"),
    text = """Welche Angestelltennummer (id) hat Max Becker?""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT id
                     |    FROM employee
                     |    WHERE firstname = 'Max' AND lastname = 'Becker';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_3: SqlExercise = Exercise(
    exerciseId = 3,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """SELECT emailaddress
                     |    FROM employee JOIN emailaddress ON employee.id = emailaddress.employee_id
                     |    WHERE firstname = 'Max' AND lastname = 'Becker';""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """SELECT emailaddress
                     |    FROM employee, emailaddress
                     |    WHERE employee.id = emailaddress.employee_id AND firstname = 'Max' AND lastname = 'Becker';""".stripMargin
        ),
        SampleSolution(
          id = 3,
          sample = """SELECT emailaddress
                     |    FROM employee as emp, emailaddress as email
                     |    WHERE emp.id = email.employee_id AND firstname = 'Max' AND lastname = 'Becker';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_4: SqlExercise = Exercise(
    exerciseId = 4,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """SELECT username
                     |    FROM employee
                     |    ORDER BY username;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_5: SqlExercise = Exercise(
    exerciseId = 5,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """SELECT chef_id, count(id)
                     |    FROM employee
                     |    WHERE chef_id IS NOT NULL
                     |    GROUP BY chef_id;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_6: SqlExercise = Exercise(
    exerciseId = 6,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """CREATE TABLE employee (
              |    id INT PRIMARY KEY,
              |    firstname VARCHAR(50),
              |    lastname VARCHAR(50),
              |    username VARCHAR(20),
              |    chef_id INT,
              |    FOREIGN KEY (chef_id) REFERENCES employee(id)
              |);"""
        )
      )
    )
  )

  private val sql_coll_1_ex_7: SqlExercise = Exercise(
    exerciseId = 7,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """UPDATE employee
                     |    SET chef_id = 3
                     |    WHERE id = 8;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_8: SqlExercise = Exercise(
    exerciseId = 8,
    collectionId = 1,
    toolId = "sql",
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
        SampleSolution(
          id = 1,
          sample = """DELETE FROM employee
                     |    WHERE id = 7;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_1_ex_9: SqlExercise = Exercise(
    exerciseId = 9,
    collectionId = 1,
    toolId = "sql",
    title = "Neue Kollegin",
    authors = Seq("bje40dc"),
    text =
      """Es gibt eine neue Angestellte mit Namen Tina Sattler.
        |Diese arbeitet für die Person mit der OID 2 und soll als OID 9 und als Nutzernamen 'tina_sattler' bekommen.""".stripMargin,
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Insert, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """INSERT INTO employee VALUES (9, 'Tina', 'Sattler', 'tina_sattler', 2);"""
        ),
        SampleSolution(
          id = 2,
          sample = """INSERT INTO employee (id, firstname, lastname, username, chef_id)
                     |VALUES (9, 'Tina', 'Sattler', 'tina_sattler', 2);""".stripMargin
        )
      )
    )
  )

  val sqlColl01Exes01To10: Seq[SqlExercise] = Seq(
    sql_coll_1_ex_1,
    sql_coll_1_ex_2,
    sql_coll_1_ex_3,
    sql_coll_1_ex_4,
    sql_coll_1_ex_5,
    sql_coll_1_ex_6,
    sql_coll_1_ex_7,
    sql_coll_1_ex_8,
    sql_coll_1_ex_9
  )

}
