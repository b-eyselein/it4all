package initialData.sql

import initialData.InitialExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType, SqlTopics}
import model.{Level, TopicWithLevel}

object SqlColl2Exes41To50 {

  private val schemaName = "amazon"

  val sql_coll_2_ex_41: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Kurzer Nachname",
    authors = Seq("bje40edc"),
    text = """Wählen Sie alle Kunden aus, deren Nachname nur aus vier Buchstaben besteht.
             |Geben Sie nur den Vornamen und Nachnamen aus.
             |Die Datensätze sollen nach dem Nachnamen alphabetisch sortiert werden.""".stripMargin
      .replace("\n", " "),
    difficulty = 3,
    topicsWithLevels = Map(
      SqlTopics.OrderBy   -> Level.Beginner,
      SqlTopics.Aggregate -> Level.Intermediate
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT first_name, family_name
          |    FROM customers
          |    WHERE LENGTH(family_name) = 4
          |    ORDER BY family_name;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_42: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Nachbestellen?",
    authors = Seq("bje40dc"),
    text = """Zeigen Sie Titel, Verlag-Name und Lagerbestand sämtlicher Bücher an,
             |deren Lagerbestand geringer als 40000 ist.""".stripMargin
      .replace("\n", " "),
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Join -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """SELECT title, publishers.name, stock
          |    FROM books JOIN publishers ON books.publisher_id = publishers.id
          |    WHERE stock < 40000;""".stripMargin,
        """SELECT title, publishers.name, stock
          |    FROM books JOIN publishers ON publisher_id = publishers.id
          |    WHERE stock < 40000;""".stripMargin
      ),
      hint = Some("""Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle 'publishers'.""")
    )
  )

  val sql_coll_2_ex_43: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Neuer Kunde",
    authors = Seq("bje40dc"),
    text = """Fügen Sie einen neuen Kunden in die Datenbank ein.
             |Name: 'Ferdinandus Merkle',
             |Email: 'ferdinandus_1856 @yahoo.com',
             |Geburtstag: '1990 - 11 - 24 '.
             |Das Passwort und die Adresse soll vorerst leer gelassen werden.""".stripMargin,
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Insert -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """INSERT INTO customers (first_name, family_name, birthday, email, password, address)
          |VALUES ('Ferdinandus', 'Merkle', '1990 - 11 - 24', 'ferdinandus_1856@yahoo.com','','');""".stripMargin,
        """INSERT INTO customers (first_name, family_name, birthday, email)
          |VALUES ('Ferdinandus', 'Merkle', '1990 - 11 - 24', 'ferdinandus_1856@yahoo.com');""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_44: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Immer diese Jugendlichen",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Kunden, die nach dem Jahr 1995 geboren wurden.",
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Delete -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """DELETE FROM customers
          |    WHERE birthday > '1995-01-01';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_45: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Karteileichen",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Kunden, die noch keine Bestellung abgegeben haben.",
    difficulty = 3,
    topicsWithLevels = Map(
      SqlTopics.SubSelect -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """DELETE FROM customers
          |    WHERE id NOT IN (SELECT customer_id FROM orders);""".stripMargin
      ),
      hint = Some("Verwenden Sie den Operator 'IN' für diese Aufgabe.")
    )
  )

  val sql_coll_2_ex_46: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Weg mit den Billigheimern",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Bücher, die mehr als 8,50 Euro kosten und die Verlags-ID 7 besitzen.",
    difficulty = 1,
    topicsWithLevels = Map(
      SqlTopics.Delete -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """DELETE FROM books
          |    WHERE price > 8.50 AND publisher_id = '7';""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_47: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Bye Bye, J. R. R!",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Bücher, die vom Autor mit der ID 3 geschrieben wurden.",
    difficulty = 3,
    topicsWithLevels = Map(
      SqlTopics.Delete -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """DELETE FROM books
          |    WHERE author_id = 3;""".stripMargin
      )
    )
  )

  val sql_coll_2_ex_48: InitialExercise[SqlExerciseContent] = InitialExercise(
    title = "Neues Buch",
    authors = Seq("bje40dc"),
    text = """Fügen Sie ein neues Buch mit dem Titel 'Applied Statistical Genetics with R ' in die Datenbank ein
             |(Autor-ID: 2, Erscheinungsjahr: 2010, Publisher-ID: '12' , Signatur 'PF / 520 - Y / 2',
             |Lagerbestand '289', Preis: '24.99 ').""".stripMargin
      .replace("\n", " "),
    difficulty = 2,
    topicsWithLevels = Map(
      SqlTopics.Insert -> Level.Beginner
    ),
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        """INSERT INTO books (title, author_id, year, publisher_id, isbn, stock, price)
          |    VALUES('Applied Statistical Genetics with R', 2, 2010, 12, 'PF/520-Y/2', 289, 24.99);""".stripMargin
      )
    )
  )

}
