package initialData.sql

import model.tools.sql.SqlTool.SqlExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType}
import model.{Exercise, SampleSolution}

object SqlColl2Exes11To20 {

  private val schemaName = "amazon"

  private val sql_coll_2_ex_11: SqlExercise = Exercise(
    exerciseId = 11,
    collectionId = 2,
    toolId = "sql",
    title = """Wer schrieb was?""",
    authors = Seq("bje40dc"),
    text = """Ordnen Sie allen Büchern ihre jeweiligen Autoren zu.
             |Geben Sie jeweils den Titel des Buches und den Nachnamen des Autoren aus!""".stripMargin
      .replace("\n", " "),
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, family_name
                     |    FROM authors JOIN books ON authors.id = books.author_id;""".stripMargin
        )
      ) /*,
        tags=[SqlExTag.SQL_JOIN]
     */
    )
  )

  private val sql_coll_2_ex_12: SqlExercise = Exercise(
    exerciseId = 12,
    collectionId = 2,
    toolId = "sql",
    title = "Bücher von Carlsen",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie Titel und Preis aller Bücher die im Verlag 'Carlsen' erschienen sind.""",
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, price
                     |    FROM books JOIN publishers ON publisher_id = publishers.id
                     |    WHERE name = 'Carlsen';""".stripMargin
        )
      ),
      hint = Some("""Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle 'publishers'.""")
      /*,
      tags=[SqlExTag.SQL_JOIN]
     */
    )
  )

  private val sql_coll_2_ex_13: SqlExercise = Exercise(
    exerciseId = 13,
    collectionId = 2,
    toolId = "sql",
    title = "Harry Potters Leben",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie Titel und ISBN sämtlicher Bücher der Autorin 'Rowling' (ohne Anführungszeichen).""",
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, isbn
                     |    FROM books JOIN authors on books.author_id = authors.id
                     |    WHERE family_name = 'Rowling'""".stripMargin
        )
      ) /*,
        tags=[SqlExTag.SQL_JOIN]
     */
    )
  )

  private val sql_coll_2_ex_14: SqlExercise = Exercise(
    exerciseId = 14,
    collectionId = 2,
    toolId = "sql",
    title = "Wilhards Wertungen",
    authors = Seq("bje40dc"),
    text =
      """Zeigen Sie die Werte aller Ratings an, die der Kunde mit der Email 'wilhard_1041@web.de' abgegeben hat.""",
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT rating
                     |    FROM ratings JOIN customers ON ratings.customer_id = customers.id
                     |    WHERE email = 'wilhard_1041@web.de';""".stripMargin
        )
      ) /*,
        tags=[SqlExTag.SQL_JOIN]
     */
    )
  )

  private val sql_coll_2_ex_15: SqlExercise = Exercise(
    exerciseId = 15,
    collectionId = 2,
    toolId = "sql",
    title = """Wer hat hier so viel bestellt?""",
    authors = Seq("bje40dc"),
    text = """Wie lauten die Nachnamen der Kunden, die mindestens 3 Bestellung aufgegeben haben?""",
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT DISTINCT family_name
                     |    FROM orders JOIN customers ON orders.customer_id = customers.id
                     |    WHERE orders.id >= 3;""".stripMargin
        )
      ),
      hint = Some("""Diese Bestellungen haben eine ID von mindestens 3.""")

      /*,
        tags=[SqlExTag.SQL_JOIN],
     */
    )
  )

  private val sql_coll_2_ex_16: SqlExercise = Exercise(
    exerciseId = 16,
    collectionId = 2,
    toolId = "sql",
    title = "GMX-Kunden",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Email-Adressen der Kunden aus die mit 'gmx.de' enden.""",
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT email
                     |    FROM customers
                     |    WHERE email LIKE '%gmx.de';""".stripMargin
        )
      ),
      hint = Some("""Verwenden Sie für die Eingrenzung der Mailadresse den 'LIKE'-Operator.""")
    )
  )

  private val sql_coll_2_ex_17: SqlExercise = Exercise(
    exerciseId = 17,
    collectionId = 2,
    toolId = "sql",
    title = "Billige Bücher",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Titel aller Bücher aus die weniger als 10,00 € kosten.""",
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title
                     |    FROM books WHERE price < 10.00;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_18: SqlExercise = Exercise(
    exerciseId = 18,
    collectionId = 2,
    toolId = "sql",
    title = "Was wird gewünscht?",
    authors = Seq("bje40dc"),
    text = """Geben die die IDs aller Bücher aus, die von Kunden gewünscht werden.
             |Achten Sie darauf, dass eine ID nur einmal vorkommt.""".stripMargin,
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT DISTINCT book_id
                     |    FROM wishlists;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_19: SqlExercise = Exercise(
    exerciseId = 19,
    collectionId = 2,
    toolId = "sql",
    title = "Jahrgang 81",
    authors = Seq("bje40dc"),
    text = """Wählen Sie die Vor- und Nachnamen der Kunden aus, die im Jahr 1981 Geburtstag haben.""",
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT first_name, family_name
                     |    FROM customers
                     |    WHERE birthday < '1982-01-01' AND birthday > '1980-12-31';""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """SELECT first_name, family_name
                     |    FROM customers
                     |    WHERE birthday LIKE '1981-%';""".stripMargin
        )
      ),
      hint = Some("""Verwenden Sie den 'LIKE'-Operator zu Vergleich des Jahres.""")
    )
  )

  private val sql_coll_2_ex_20: SqlExercise = Exercise(
    exerciseId = 20,
    collectionId = 2,
    toolId = "sql",
    title = "Geringer Bestand oder Preis",
    authors = Seq("bje40dc"),
    text = """Geben Sie die Titel aller Bücher aus, die einen Bestand von weniger als 20.000 Exemplaren
             |oder einen Preis unter 14€ haben.""".stripMargin.replace("\n", " "),
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title
                     |    FROM books
                     |    WHERE stock < 20000 OR price < 14.00;""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """SELECT title
                     |    FROM books
                     |    WHERE stock < 20000 OR price < 14;""".stripMargin
        )
      )
    )
  )

  val sqlColl2Exes11To20 = Seq(
    sql_coll_2_ex_11,
    sql_coll_2_ex_12,
    sql_coll_2_ex_13,
    sql_coll_2_ex_14,
    sql_coll_2_ex_15,
    sql_coll_2_ex_16,
    sql_coll_2_ex_17,
    sql_coll_2_ex_18,
    sql_coll_2_ex_19,
    sql_coll_2_ex_20
  )

}
