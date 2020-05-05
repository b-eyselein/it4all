package initialData.sql

import model.tools.sql.SqlTool.SqlExercise
import model.tools.sql.{SqlExerciseContent, SqlExerciseType}
import model.{Exercise, SampleSolution}

object SqlColl2Exes41To50 {

  private val schemaName = "amazon"

  private val sql_coll_2_ex_41: SqlExercise = Exercise(
    exerciseId = 41,
    collectionId = 2,
    toolId = "sql",
    title = "Kurzer Nachname",
    authors = Seq("bje40edc"),
    text = """Wählen Sie alle Kunden aus, deren Nachname nur aus vier Buchstaben besteht.
             |Geben Sie nur den Vornamen und Nachnamen aus.
             |Die Datensätze sollen nach dem Nachnamen alphabetisch sortiert werden.""".stripMargin
      .replace("\n", " "),
    topicAbbreviations = Seq.empty,
    difficulty = 3,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT first_name, family_name
                     |    FROM customers
                     |    WHERE LENGTH(family_name) = 4
                     |    ORDER BY family_name;""".stripMargin
        )
      ) /*,
        tags=Seq(SqlExTag.SQL_ORDER_BY)
     */
    )
  )

  private val sql_coll_2_ex_42: SqlExercise = Exercise(
    exerciseId = 42,
    collectionId = 2,
    toolId = "sql",
    title = "Nachbestellen?",
    authors = Seq("bje40dc"),
    text = """Zeigen Sie Titel, Verlag-Name und Lagerbestand sämtlicher Bücher an,
             |deren Lagerbestand geringer als 40000 ist.""".stripMargin
      .replace("\n", " "),
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.SELECT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """SELECT title, publishers.name, stock
                     |    FROM books JOIN publishers ON publisher_id = publishers.id
                     |    WHERE stock < 40000;""".stripMargin
        )
      ),
      hint = Some("""Die Zuordnung von Verlag-Id zu Verlag-Name befindet sich in der Tabelle 'publishers'.""") /*,
        tags=Seq(SqlExTag.SQL_JOIN)
     */
    )
  )

  private val sql_coll_2_ex_43: SqlExercise = Exercise(
    exerciseId = 43,
    collectionId = 2,
    toolId = "sql",
    title = "Neuer Kunde",
    authors = Seq("bje40dc"),
    text = """Fügen Sie einen neuen Kunden in die Datenbank ein.
             |Name: 'Ferdinandus Merkle',
             |Email: 'ferdinandus_1856 @yahoo.com',
             |Geburtstag: '1990 - 11 - 24 '.
             |Das Passwort und die Adresse soll vorerst leer gelassen werden.""".stripMargin,
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """INSERT INTO customers (first_name, family_name, birthday, email, password, address)
                     |VALUES ('Ferdinandus', 'Merkle', '1990 - 11 - 24', 'ferdinandus_1856@yahoo.com','','');""".stripMargin
        ),
        SampleSolution(
          id = 2,
          sample = """INSERT INTO customers (first_name, family_name, birthday, email)
                     |VALUES ('Ferdinandus', 'Merkle', '1990 - 11 - 24', 'ferdinandus_1856@yahoo.com');""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_44: SqlExercise = Exercise(
    exerciseId = 44,
    collectionId = 2,
    toolId = "sql",
    title = "Immer diese Jugendlichen",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Kunden, die nach dem Jahr 1995 geboren wurden.",
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """DELETE FROM customers
                     |    WHERE birthday > '1995-01-01';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_45: SqlExercise = Exercise(
    exerciseId = 45,
    collectionId = 2,
    toolId = "sql",
    title = "Karteileichen",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Kunden, die noch keine Bestellung abgegeben haben.",
    topicAbbreviations = Seq.empty,
    difficulty = 3,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """DELETE FROM customers
                     |    WHERE id NOT IN (SELECT customer_id FROM orders);""".stripMargin
        )
      ),
      hint = Some("Verwenden Sie den Operator 'IN' für diese Aufgabe.")
    )
  )

  private val sql_coll_2_ex_46: SqlExercise = Exercise(
    exerciseId = 46,
    collectionId = 2,
    toolId = "sql",
    title = "Weg mit den Billigheimern",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Bücher, die mehr als 8,50 Euro kosten und die Verlags-ID 7 besitzen.",
    topicAbbreviations = Seq.empty,
    difficulty = 1,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """DELETE FROM books
                     |    WHERE price > 8.50 AND publisher_id = '7';""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_47: SqlExercise = Exercise(
    exerciseId = 47,
    collectionId = 2,
    toolId = "sql",
    title = "Bye Bye, J. R. R!",
    authors = Seq("bje40dc"),
    text = "Löschen Sie alle Bücher, die vom Autor mit der ID 3 geschrieben wurden.",
    topicAbbreviations = Seq.empty,
    difficulty = 3,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.DELETE,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """DELETE FROM books
                     |    WHERE author_id = 3;""".stripMargin
        )
      )
    )
  )

  private val sql_coll_2_ex_48: SqlExercise = Exercise(
    exerciseId = 48,
    collectionId = 2,
    toolId = "sql",
    title = "Neues Buch",
    authors = Seq("bje40dc"),
    text = """Fügen Sie ein neues Buch mit dem Titel 'Applied Statistical Genetics with R ' in die Datenbank ein
             |(Autor-ID: 2, Erscheinungsjahr: 2010, Publisher-ID: '12' , Signatur 'PF / 520 - Y / 2',
             |Lagerbestand '289', Preis: '24.99 ').""".stripMargin
      .replace("\n", " "),
    topicAbbreviations = Seq.empty,
    difficulty = 2,
    content = SqlExerciseContent(
      exerciseType = SqlExerciseType.INSERT,
      schemaName = schemaName,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = """INSERT INTO books (title, author_id, year, publisher_id, isbn, stock, price)
                     |    VALUES('Applied Statistical Genetics with R', 2, 2010, 12, 'PF/520-Y/2', 289, 24.99);""".stripMargin
        )
      )
    )
  )

  val sqlColl2Exes41To50 = Seq(
    sql_coll_2_ex_41,
    sql_coll_2_ex_42,
    sql_coll_2_ex_43,
    sql_coll_2_ex_44,
    sql_coll_2_ex_45,
    sql_coll_2_ex_46,
    sql_coll_2_ex_47,
    sql_coll_2_ex_48
  )

}
