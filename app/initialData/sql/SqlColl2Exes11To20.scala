package initialData.sql

import initialData.InitialExercise
import model.Level
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}

object SqlColl2Exes11To20 {

  private val schemaName = "amazon"

  val sql_coll_2_ex_11: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Wer schrieb was?""",
    authors = Seq("bje40dc"),
    text = """Ordnen Sie allen Büchern ihre jeweiligen Autoren zu.
             |Geben Sie jeweils den Titel des Buches und den Nachnamen des Autoren aus!""".stripMargin
      .replace("\n", " "),
    difficulty = 1,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, family_name
          |    FROM authors JOIN books ON authors.id = books.author_id;""".stripMargin,
        """SELECT title, family_name
          |    FROM authors JOIN books ON authors.id = author_id;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_12: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Bücher von Carlsen",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie Titel und Preis aller Bücher die im Verlag 'Carlsen' erschienen sind.""",
    difficulty = 1,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, price
          |    FROM books JOIN publishers ON books.publisher_id = publishers.id
          |    WHERE name = 'Carlsen';""".stripMargin,
        """SELECT title, price
          |    FROM books JOIN publishers ON publisher_id = publishers.id
          |    WHERE name = 'Carlsen';""".stripMargin
      ),
      hint = Some("""Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle 'publishers'.""")
    )
  )

  val sql_coll_2_ex_13: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Harry Potters Leben",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie Titel und ISBN sämtlicher Bücher der Autorin 'Rowling' (ohne Anführungszeichen).""",
    difficulty = 1,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, isbn
          |    FROM books JOIN authors on books.author_id = authors.id
          |    WHERE family_name = 'Rowling'""".stripMargin,
        """SELECT title, isbn
          |    FROM books JOIN authors on author_id = authors.id
          |    WHERE family_name = 'Rowling'""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_14: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Wilhards Wertungen",
    authors = Seq("bje40dc"),
    text = """Zeigen Sie die Werte aller Ratings an, die der Kunde mit der Email 'wilhard_1041@web.de' abgegeben hat.""",
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT rating
          |    FROM ratings JOIN customers ON ratings.customer_id = customers.id
          |    WHERE email = 'wilhard_1041@web.de';""".stripMargin,
        """SELECT rating
          |    FROM ratings JOIN customers ON customer_id = customers.id
          |    WHERE email = 'wilhard_1041@web.de';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_15: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = """Wer hat hier so viel bestellt?""",
    authors = Seq("bje40dc"),
    text = """Wie lauten die Nachnamen der Kunden, die mindestens 3 Bestellung aufgegeben haben?""",
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT DISTINCT family_name
          |    FROM orders JOIN customers ON orders.customer_id = customers.id
          |    WHERE orders.id >= 3;""".stripMargin,
        """SELECT DISTINCT family_name
          |    FROM orders JOIN customers ON customer_id = customers.id
          |    WHERE orders.id >= 3;""".stripMargin
      ),
      hint = Some("""Diese Bestellungen haben eine ID von mindestens 3.""")
    )
  )

  val sql_coll_2_ex_16: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "GMX-Kunden",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Email-Adressen der Kunden aus die mit 'gmx.de' enden.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT email
          |    FROM customers
          |    WHERE email LIKE '%gmx.de';""".stripMargin
      ),
      hint = Some("""Verwenden Sie für die Eingrenzung der Mailadresse den 'LIKE'-Operator.""")
    )
  )

  val sql_coll_2_ex_17: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Billige Bücher",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Titel aller Bücher aus die weniger als 10,00 € kosten.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title
          |    FROM books WHERE price < 10.00;""".stripMargin,
        """SELECT title
          |    FROM books WHERE price < 10;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_18: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Was wird gewünscht?",
    authors = Seq("bje40dc"),
    text = """Geben die die IDs aller Bücher aus, die von Kunden gewünscht werden.
             |Achten Sie darauf, dass eine ID nur einmal vorkommt.""".stripMargin,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT DISTINCT book_id
          |    FROM wishlists;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_19: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Jahrgang 81",
    authors = Seq("bje40dc"),
    text = """Wählen Sie die Vor- und Nachnamen der Kunden aus, die im Jahr 1981 Geburtstag haben.""",
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name
          |    FROM customers
          |    WHERE birthday < '1982-01-01' AND birthday > '1980-12-31';""".stripMargin,
        """SELECT first_name, family_name
          |    FROM customers
          |    WHERE birthday LIKE '1981-%';""".stripMargin
      ),
      hint = Some("""Verwenden Sie den 'LIKE'-Operator zu Vergleich des Jahres.""")
    )
  )

  val sql_coll_2_ex_20: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Geringer Bestand oder Preis",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Titel aller Bücher aus, die einen Bestand von weniger als 20.000 Exemplaren
             |oder einen Preis unter 14€ haben.""".stripMargin.replace("\n", " "),
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title
          |    FROM books
          |    WHERE stock < 20000 OR price < 14.00;""".stripMargin,
        """SELECT title
          |    FROM books
          |    WHERE stock < 20000 OR price < 14;""".stripMargin
      )
    )
  )

}
