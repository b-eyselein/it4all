package initialData.sql

import model.tools.sql.SqlTool.SqlExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}
import model.{Exercise, Level, SampleSolution, TopicWithLevel}

object SqlColl2Exes21To30 {

  private val schemaName = "amazon"

  private val sql_coll_2_ex_21: SqlExercise = Exercise(
    exerciseId = 21,
    collectionId = 2,
    toolId = "sql",
    title = "Sterne in der Wüste 1",
    authors = Seq("bje40dc"),
    text = """Wie oft wurde das Buch 'Die Stadt in der Wüste' bewertet? Nennen Sie die Spalte 'Anzahl'.""",
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Beginner),
      TopicWithLevel(SqlTopics.Join, Level.Beginner),
      TopicWithLevel(SqlTopics.Alias, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT COUNT(*) AS Anzahl
                     |    FROM ratings JOIN books ON books.id = ratings.book_id
                     |    WHERE title = 'Die Stadt in der Wüste';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_22: SqlExercise = Exercise(
    exerciseId = 22,
    collectionId = 2,
    toolId = "sql",
    title = "Sterne in der Wüste 2",
    authors = Seq("bje40dc"),
    text = """Welche Durchschnittsbewertung bekam das Buch 'Die Stadt in der Wüste'?
             |Nennen Sie die Spalte 'Durchschnitt'.""".stripMargin.replace("\n", " "),
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Beginner),
      TopicWithLevel(SqlTopics.Join, Level.Beginner),
      TopicWithLevel(SqlTopics.Alias, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT AVG(rating) AS Durchschnitt
                     |    FROM ratings JOIN books ON books.id = ratings.book_id
                     |    WHERE title = 'Die Stadt in der Wüste';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_23: SqlExercise = Exercise(
    exerciseId = 23,
    collectionId = 2,
    toolId = "sql",
    title = "Harry Potters",
    authors = Seq("bje40dc"),
    text = """Wie hoch ist der Gesamtbestand an Harry Potter-Büchern? Nennen Sie die Spalte 'Gesamtbestand'.""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Beginner),
      TopicWithLevel(SqlTopics.Alias, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT SUM(stock) AS Gesamtbestand
                     |    FROM books
                     |    WHERE title like 'Harry Potter%';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_24: SqlExercise = Exercise(
    exerciseId = 24,
    collectionId = 2,
    toolId = "sql",
    title = "Fan oder nicht?",
    authors = Seq("bje40dc"),
    text =
      """Geben Sie jeweils die schlechteste und beste Bewertung des Buches 'Harry Potter und der Halbblutprinz' aus.
        |Nennen Sie die Spalten jeweils 'Schlechteste' und 'Beste'.""".stripMargin.replace("\n", " "),
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Intermediate),
      TopicWithLevel(SqlTopics.Alias, Level.Intermediate),
      TopicWithLevel(SqlTopics.Join, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT min(rating) AS Schlechteste, max(rating) AS Beste
                     |    FROM books JOIN ratings ON books.id = ratings.book_id
                     |    WHERE title = 'Harry Potter und der Halbblutprinz';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_25: SqlExercise = Exercise(
    exerciseId = 25,
    collectionId = 2,
    toolId = "sql",
    title = "Durchschnittlicher Bestand",
    authors = Seq("bje40dc"),
    text = """Wie hoch ist der durchschnittliche Bestand aller Bücher? Nennen Sie die Spalte 'Durchschnitt'.""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Aggregate, Level.Beginner),
      TopicWithLevel(SqlTopics.Join, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT AVG(stock) AS Durchschnitt
                     |    FROM books;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_26: SqlExercise = Exercise(
    exerciseId = 26,
    collectionId = 2,
    toolId = "sql",
    title = "Wunsch des Phönix",
    authors = Seq("bje40dc"),
    text =
      """Wie lauten die Nachnamen der Kunden die sich das Buch 'Harry Potter und der Orden des Phönix' wünschen?""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Join, Level.Intermediate)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT family_name
                     |    FROM customers
                     |        JOIN wishlists ON wishlists.customer_id = customers.id
                     |        JOIN books on wishlists.book_id = books.id
                     |    WHERE title = 'Harry Potter und der Orden des Phönix';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_27: SqlExercise = Exercise(
    exerciseId = 27,
    collectionId = 2,
    toolId = "sql",
    title = "Orwellianisch",
    authors = Seq("bje40dc"),
    text = """Suchen Sie die Titel aller Bücher, deren Autor George Orwell ist.
             |Ordnen Sie die Titel nach Erscheinungsjahr abwärts.""".stripMargin
      .replace("\n", " "),
    difficulty = 3,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.OrderBy, Level.Beginner),
      TopicWithLevel(SqlTopics.Join, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title
                     |    FROM books JOIN authors on books.author_id = authors.id
                     |    WHERE first_name = 'George' AND family_name = 'Orwell'
                     |    ORDER BY year DESC;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_28: SqlExercise = Exercise(
    exerciseId = 28,
    collectionId = 2,
    toolId = "sql",
    title = "Teuer, teuer und teuer",
    authors = Seq("bje40dc"),
    text = """Zeigen sie Titel und Autor-ID der drei teuersten Bücher an.
             |Sortieren Sie die Einträge nach Preis abwärts.""".stripMargin.replace("\n", " "),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.OrderBy, Level.Beginner),
      TopicWithLevel(SqlTopics.Limit, Level.Beginner)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, author_id
                     |    FROM books
                     |    ORDER BY price DESC
                     |    LIMIT 3;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_29: SqlExercise = Exercise(
    exerciseId = 29,
    collectionId = 2,
    toolId = "sql",
    title = "Wer will Harry?",
    authors = Seq("bje40dc"),
    text =
      """Wählen Sie alle Bestellungen aus, die das Buch 'Harry Potter und der Halbblutprinz' enthalten.
        |Geben Sie für diese Bestellungen jeweils das Datum und die Anzahl der bestellten Exemplare des Buches aus.""".stripMargin
        .replace("\n", " "),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Join, Level.Intermediate)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT date, amount
                     |    FROM orders
                     |        JOIN order_positions ON orders.id = order_id
                     |        JOIN books ON books.id = book_id
                     |    WHERE title = 'Harry Potter und der Halbblutprinz';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_30: SqlExercise = Exercise(
    exerciseId = 30,
    collectionId = 2,
    toolId = "sql",
    title = "Emails mit G",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Email-Adressen der Kunden aus die mit 'gmx.de ' oder mit 'gmail.com' enden.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT email
                     |    FROM customers
                     |    WHERE email LIKE '%gmx.de' OR email LIKE '%gmail.com';""".stripMargin
        )
      )
    )
  )

  val sqlColl2Exes21To30 = Seq(
    sql_coll_2_ex_21,
    sql_coll_2_ex_22,
    sql_coll_2_ex_23,
    sql_coll_2_ex_24,
    sql_coll_2_ex_25,
    sql_coll_2_ex_26,
    sql_coll_2_ex_27,
    sql_coll_2_ex_28,
    sql_coll_2_ex_29,
    sql_coll_2_ex_30
  )

}
