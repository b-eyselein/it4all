package initialData.sql

import initialData.InitialExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType}

object SqlColl2Exes01To10 {

  private val schemaName = "amazon"

  val sql_coll_2_ex_01: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Alles über die Autoren",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Spalten der Autorentabelle aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT *
          |    FROM authors;""".stripMargin,
        """SELECT id, first_name, family_name, birthday
          |    FROM authors;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_02: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Nachnamen aller Autoren",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Nachnamen aller Autoren aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT family_name
          |    FROM authors;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_03: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Verlagsnamen",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Namen aller Verlage aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT name
          |    FROM publishers;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_04: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Namen aller Kunden",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Vor- und Nachnamen aller Kunden aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name
          |    FROM customers;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_05: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Daten über Bücher""",
    authors = Seq("bje40dc"),
    text = """Geben Sie für jedes Buch jeweils den Titel das Erscheinungsjahr und die ISBN aus!""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, year, isbn
          |    FROM books;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_06: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Titelsuche""",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie den Titel des Buches mit der ISBN '978-3551354051' (ohne Anführungszeichen).""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title
          |    FROM books
          |    WHERE isbn = '978-3551354051';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_07: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Preissuche",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie den Preis des Buches mit der ISBN '978-3551354068' (ohne Anführungszeichen).",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT price
          |    FROM books
          |    WHERE isbn = '978-3551354068';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_08: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Autorensuche",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie den Vor- und Nachnamen des Autoren mit der ID 3.",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name
          |    FROM authors
          |    WHERE id = 3;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_09: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Der kleine Prinz?""",
    authors = Seq("bje40dc"),
    text = "Bestimmen Sie die Autor-ID des Buches 'Der kleine Prinz' (ohne Anführungszeichen).",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT author_id
          |    FROM books
          |    WHERE title = 'Der kleine Prinz';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_10: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Verlag mit Telefonnummer",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie den Namen des Verlages der unter der Telefonnummer '+49 2402 / 806341' erreichbar ist.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT name
          |    FROM publishers
          |    WHERE phone = '+49 2402 / 806341';""".stripMargin
      )
    )
  )

}
