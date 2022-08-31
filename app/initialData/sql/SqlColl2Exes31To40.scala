package initialData.sql

import initialData.InitialExercise
import model.Level
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}

object SqlColl2Exes31To40 {

  private val schemaName = "amazon"

  val sql_coll_2_ex_31: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Tolkien in Billig",
    authors = Seq("bje40dc"),
    text = """Bestimmen Sie Titel und Preis aller Bücher des Autors 'Tolkien' deren Preis über 10€ liegt.
             |Ordnen Sie die Einträge nach Erscheinungsjahr abwärts.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Advanced,
    topicsWithLevels = Map(
      SqlTopics.OrderBy -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, price
          |    FROM books JOIN authors on authors.id = books.author_id
          |    WHERE authors.family_name = 'Tolkien' AND price > 10
          |    ORDER BY year DESC;""".stripMargin,
        """SELECT title, price
          |    FROM books JOIN authors on authors.id = author_id
          |    WHERE authors.family_name = 'Tolkien' AND price > 10
          |    ORDER BY year DESC;""".stripMargin,
        """SELECT title, price
          |    FROM books JOIN authors on authors.id = books.author_id
          |    WHERE family_name = 'Tolkien' AND price > 10
          |    ORDER BY year DESC;""".stripMargin,
        """SELECT title, price
          |    FROM books JOIN authors on authors.id = author_id
          |    WHERE family_name = 'Tolkien' AND price > 10
          |    ORDER BY year DESC;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_32: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Billig gekauft",
    authors = Seq("bje40dc"),
    text = """Berechnen Sie die Anzahl der bestellten Buchexemplare (Tabelle order_positions), die zum Zeitpunkt des Kaufes
             |weniger als fünf Euro gekostet haben. Geben Sie das Ergebnis unter dem Spaltenname 'Anzahl' aus.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Intermediate,
    topicsWithLevels = Map(
      SqlTopics.Aggregate -> Level.Beginner,
      SqlTopics.Alias     -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT SUM(amount) AS Anzahl
          |    FROM order_positions
          |    WHERE price < 5;""".stripMargin,
        """SELECT SUM(amount) AS Anzahl
          |    FROM order_positions
          |    WHERE price < 5.00;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_33: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Kumulierter Bestand",
    authors = Seq("bje40dc"),
    text = """Erfassen Sie die ID aller Autoren und den kumulierten Bestand ihrer Bücher.
             |Ordnen Sie die Einträge nach kumulierten Bestand abwärts.
             |Benennen Sie die Spalte des kumulierten Bestandes 'stock_sum' (ohne Anführungszeichen).""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Advanced,
    topicsWithLevels = Map(
      SqlTopics.Aggregate -> Level.Intermediate,
      SqlTopics.GroupBy   -> Level.Beginner,
      SqlTopics.OrderBy   -> Level.Intermediate
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT author_id, sum(stock) AS stock_sum
          |    FROM books
          |    GROUP BY author_id
          |    ORDER BY stock_sum DESC;""".stripMargin
      ),
      hint = Some("""Verwenden Sie den Sum-Operator und das Schlüsselwort AS.""")
    )
  )

  val sql_coll_2_ex_34: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Februargeburtstag",
    authors = Seq("bje40dc"),
    text = """Wählen Sie alle Kunden aus die im Februar Geburtstag haben und geben Sie den Vornamen,
             |Nachnamen und das Geburtsdatum aus.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Beginner,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name, birthday
          |    FROM customers
          |    WHERE birthday LIKE '%-02-%';""".stripMargin
      ),
      hint = Some("""Verwenden Sie für die Eingrenzung des Geburtsdatums den 'LIKE'-Operator.""")
    )
  )

  val sql_coll_2_ex_35: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Ich bin der Jüngste!",
    authors = Seq("bje40dc"),
    text = """Geben Sie den Vornamen, Nachnamen und Geburtstag des jüngsten Kunden aus.""",
    difficulty = Level.Intermediate,
    topicsWithLevels = Map(
      SqlTopics.OrderBy -> Level.Intermediate,
      SqlTopics.Limit   -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name, birthday
          |    FROM customers
          |    ORDER BY birthday DESC
          |    LIMIT 1;""".stripMargin
      ),
      hint = Some("Achten Sie darauf, dass sich die Spaltennamen nicht verändern.")
    )
  )

  val sql_coll_2_ex_36: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Passwortupdate!",
    authors = Seq("bje40dc"),
    text = """Sie möchten ihre Kunden dazu motivieren sichere Passwörter zu verwenden.
             |Alle Kunden, die als Kennwort 'Passwort' verwenden, sollen beim nächsten Login über eine Meldung aufgefordert
             |werden, sich ein neues auszudenken.
             |Geben Sie die ID und die Email-Adresse der faulen Kunden aus.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Beginner,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT id, email
          |    FROM customers
          |    WHERE password = '3e45af4ca27ea2b03fc6183af40ea112';""".stripMargin
      ),
      hint = Some(
        """Die Passwörter der Kunden sind als MD5-Hash-String in der Tabelle 'customers' gespeichert.
          |Der MD5-Hashstring zu 'Passwort' lautet '3e45af4ca27ea2b03fc6183af40ea112'.""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_37: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Mit allem zufrieden?",
    authors = Seq("bje40dc"),
    text = """Ermitteln Sie den Kunden, welcher die höchste durchschnittliche Bewertung abgegeben hat.
             |Geben Sie dazu Vorname, Nachname und seine durchschnittliche Bewertung (Spaltenbezeichnung 'avg_rating') aus.""".stripMargin,
    difficulty = Level.Advanced,
    topicsWithLevels = Map(
      SqlTopics.Join      -> Level.Beginner,
      SqlTopics.Aggregate -> Level.Intermediate,
      SqlTopics.GroupBy   -> Level.Beginner,
      SqlTopics.OrderBy   -> Level.Intermediate,
      SqlTopics.Limit     -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT customers.first_name, customers.family_name, AVG(rating) AS avg_rating
          |    FROM ratings
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY customer_id
          |    ORDER BY avg_rating DESC
          |    LIMIT 1;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, AVG(rating) AS avg_rating
          |    FROM ratings
          |        LEFT JOIN customers ON customers.id = rating.customer_id
          |    GROUP BY customer_id
          |    ORDER BY avg_rating DESC
          |    LIMIT 1;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, AVG(rating) AS avg_rating
          |    FROM ratings
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY rating.customer_id
          |    ORDER BY avg_rating DESC
          |    LIMIT 1;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, AVG(rating) AS avg_rating
          |    FROM ratings
          |        LEFT JOIN customers ON customers.id = rating.customer_id
          |    GROUP BY rating.customer_id
          |    ORDER BY avg_rating DESC
          |    LIMIT 1;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_38: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Teure Wünsche...",
    authors = Seq("bje40dc"),
    text = """Ermitteln Sie den Einkaufswert der Wunschlisten für jeden Kunden.
             |Listen Sie dabei Vorname, Nachname und Einkaufswert (Spaltenbezeichnung 'value') auf.
             |Sortieren Sie die Einträge aufwärts nach Einkaufswert.""".stripMargin
      .replace("\n", " "),
    difficulty = Level.Expert,
    topicsWithLevels = Map(
      SqlTopics.Aggregate -> Level.Intermediate,
      SqlTopics.Alias     -> Level.Beginner,
      SqlTopics.Join      -> Level.Advanced,
      SqlTopics.GroupBy   -> Level.Intermediate,
      SqlTopics.OrderBy   -> Level.Intermediate
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = wishlists.book_id
          |        LEFT JOIN customers ON customers.id = wishlists.customer_id
          |    GROUP BY wishlists.customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = book_id
          |        LEFT JOIN customers ON customers.id = wishlists.customer_id
          |    GROUP BY wishlists.customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = wishlists.book_id
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY wishlists.customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = book_id
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY wishlists.customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = wishlists.book_id
          |        LEFT JOIN customers ON customers.id = wishlists.customer_id
          |    GROUP BY customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = book_id
          |        LEFT JOIN customers ON customers.id = wishlists.customer_id
          |    GROUP BY customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = wishlists.book_id
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY customer_id
          |    ORDER BY value;""".stripMargin,
        """SELECT customers.first_name, customers.family_name, SUM(price) AS value
          |    FROM wishlists
          |        LEFT JOIN books ON books.id = book_id
          |        LEFT JOIN customers ON customers.id = customer_id
          |    GROUP BY customer_id
          |    ORDER BY value;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_39: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Veröffentlichungsjahre",
    authors = Seq("bje40dc"),
    text = """Wählen Sie alle Bücher aus, die in den Jahren 1998, 2001 oder 2011 veröffentlicht wurden.""",
    difficulty = Level.Intermediate,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT * FROM books
          |    WHERE year IN (1998, 2001, 2011);""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_40: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Aufgestockt",
    authors = Seq("bje40dc"),
    text = """Es sind neue Exemplare des Buches mit dem Titel 1894 eingetroffen.
             |Aktualisieren Sie den Bestand auf den neuen Wert von 500.""".stripMargin.replace("\n", " "),
    difficulty = Level.Beginner,
    topicsWithLevels = Map(
      SqlTopics.Update -> Level.Intermediate
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.UPDATE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """UPDATE books
          |    SET stock = 500
          |    WHERE title = '1984';""".stripMargin
      )
    )
  )

}
