package initialData.sql

import initialData.InitialExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}
import model.{Level, TopicWithLevel}

object SqlColl2Exes21To30 {

  private val schemaName = "amazon"

  val sql_coll_2_ex_21: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT COUNT(*) AS Anzahl
          |    FROM ratings JOIN books ON books.id = ratings.book_id
          |    WHERE title = 'Die Stadt in der Wüste';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_22: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT AVG(rating) AS Durchschnitt
          |    FROM ratings JOIN books ON books.id = ratings.book_id
          |    WHERE title = 'Die Stadt in der Wüste';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_23: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT SUM(stock) AS Gesamtbestand
          |    FROM books
          |    WHERE title like 'Harry Potter%';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_24: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Fan oder nicht?",
    authors = Seq("bje40dc"),
    text = """Geben Sie jeweils die schlechteste und beste Bewertung des Buches 'Harry Potter und der Halbblutprinz' aus.
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
        """SELECT min(rating) AS Schlechteste, max(rating) AS Beste
          |    FROM books JOIN ratings ON books.id = ratings.book_id
          |    WHERE title = 'Harry Potter und der Halbblutprinz';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_25: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT AVG(stock) AS Durchschnitt
          |    FROM books;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_26: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Wunsch des Phönix",
    authors = Seq("bje40dc"),
    text = """Wie lauten die Nachnamen der Kunden die sich das Buch 'Harry Potter und der Orden des Phönix' wünschen?""",
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(SqlTopics.Join, Level.Intermediate)
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT family_name
          |    FROM customers
          |        JOIN wishlists ON wishlists.customer_id = customers.id
          |        JOIN books on wishlists.book_id = books.id
          |    WHERE title = 'Harry Potter und der Orden des Phönix';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_27: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT title
          |    FROM books JOIN authors on books.author_id = authors.id
          |    WHERE first_name = 'George' AND family_name = 'Orwell'
          |    ORDER BY year DESC;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_28: InitialExercise[SqlExerciseContent] = InitialExercise(
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
        """SELECT title, author_id
          |    FROM books
          |    ORDER BY price DESC
          |    LIMIT 3;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_29: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Wer will Harry?",
    authors = Seq("bje40dc"),
    text = """Wählen Sie alle Bestellungen aus, die das Buch 'Harry Potter und der Halbblutprinz' enthalten.
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
        """SELECT date, amount
          |    FROM orders
          |        JOIN order_positions ON orders.id = order_id
          |        JOIN books ON books.id = book_id
          |    WHERE title = 'Harry Potter und der Halbblutprinz';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_30: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Emails mit G",
    authors = Seq("bje40dc"),
    text = """Geben Sie alle Email-Adressen der Kunden aus die mit 'gmx.de ' oder mit 'gmail.com' enden.""",
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT email
          |    FROM customers
          |    WHERE email LIKE '%gmx.de' OR email LIKE '%gmail.com';""".stripMargin
      )
    )
  )

}
