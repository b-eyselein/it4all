package initialData.sql

import model.tools.sql.SqlTool.SqlExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType}
import model.{Exercise, SampleSolution}

object SqlColl2Exes01To10 {

  private val schemaName = "amazon"

  private val sql_coll_2_ex_01: SqlExercise = Exercise(
    exerciseId = 1,
    collectionId = 2,
    toolId = "sql",
    title = "Alles über die Autoren",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Spalten der Autorentabelle aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT *
                     |    FROM authors;""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """SELECT id, first_name, family_name, birthday
                     |    FROM authors;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_02: SqlExercise = Exercise(
    exerciseId = 2,
    collectionId = 2,
    toolId = "sql",
    title = "Nachnamen aller Autoren",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Nachnamen aller Autoren aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT family_name
                     |    FROM authors;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_03: SqlExercise = Exercise(
    exerciseId = 3,
    collectionId = 2,
    toolId = "sql",
    title = "Verlagsnamen",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Namen aller Verlage aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT name
                     |    FROM publishers;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_04: SqlExercise = Exercise(
    exerciseId = 4,
    collectionId = 2,
    toolId = "sql",
    title = "Namen aller Kunden",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Vor- und Nachnamen aller Kunden aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT first_name, family_name
                     |    FROM customers;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_05: SqlExercise = Exercise(
    exerciseId = 5,
    collectionId = 2,
    toolId = "sql",
    title = """Daten über Bücher""",
    authors = Seq("bje40dc"),
    text = """Geben Sie für jedes Buch jeweils den Titel das Erscheinungsjahr und die ISBN aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, year, isbn
                     |    FROM books;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_06: SqlExercise = Exercise(
    exerciseId = 6,
    collectionId = 2,
    toolId = "sql",
    title = """Titelsuche""",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie den Titel des Buches mit der ISBN '978-3551354051' (ohne Anführungszeichen).""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title
                     |    FROM books
                     |    WHERE isbn = '978-3551354051';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_07: SqlExercise = Exercise(
    exerciseId = 7,
    collectionId = 2,
    toolId = "sql",
    title = "Preissuche",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie den Preis des Buches mit der ISBN '978-3551354068' (ohne Anführungszeichen).",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT price
                     |    FROM books
                     |    WHERE isbn = '978-3551354068';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_08: SqlExercise = Exercise(
    exerciseId = 8,
    collectionId = 2,
    toolId = "sql",
    title = "Autorensuche",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie den Vor- und Nachnamen des Autoren mit der ID 3.",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT first_name, family_name
                     |    FROM authors
                     |    WHERE id = 3;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_09: SqlExercise = Exercise(
    exerciseId = 9,
    collectionId = 2,
    toolId = "sql",
    title = """Der kleine Prinz?""",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie die Autor-ID des Buches 'Der kleine Prinz' (ohne Anführungszeichen).",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT author_id
                     |    FROM books
                     |    WHERE title = 'Der kleine Prinz';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_10: SqlExercise = Exercise(
    exerciseId = 10,
    collectionId = 2,
    toolId = "sql",
    title = "Verlag mit Telefonnummer",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie den Namen des Verlages der unter der Telefonnummer '+49 2402 / 806341' erreichbar ist.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT name
                     |    FROM publishers
                     |    WHERE phone = '+49 2402 / 806341';""".stripMargin
        )
      )
    )
  )

  val sqlColl2Exes01To10 = Seq(
    sql_coll_2_ex_01,
    sql_coll_2_ex_02,
    sql_coll_2_ex_03,
    sql_coll_2_ex_04,
    sql_coll_2_ex_05,
    sql_coll_2_ex_06,
    sql_coll_2_ex_07,
    sql_coll_2_ex_08,
    sql_coll_2_ex_09,
    sql_coll_2_ex_10
  )

}
