package initialData.xml

import better.files.File
import initialData.InitialData._
import initialData.{InitialCollection, InitialData, InitialExercise}
import model.Level
import model.tools.xml.{XmlExerciseContent, XmlSolution}

object XmlInitialData {

  private val toolId = "xml"

  private val xmlColl1Ex1 = {
    val exResPath: File = exerciseResourcesPath(toolId, 1, 1)

    InitialExercise(
      title = "Party",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument",
      difficulty = Level.Beginner,
      content = XmlExerciseContent(
        rootNode = "party",
        grammarDescription = loadTextFromFile(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_1" / "party.dtd"),
            document = loadTextFromFile(exResPath / "sol_1" / "party.xml")
          )
        )
      )
    )
  }

  private val xmlColl1Ex2 = {
    val exResPath: File = exerciseResourcesPath(toolId, 1, 2)

    InitialExercise(
      title = "Vorlesung",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument.",
      difficulty = Level.Intermediate,
      content = XmlExerciseContent(
        rootNode = "lecture",
        grammarDescription = loadTextFromFile(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_1" / "lecture.dtd"),
            document = loadTextFromFile(exResPath / "sol_1" / "lecture.xml")
          ),
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_2" / "lecture.dtd"),
            document = loadTextFromFile(exResPath / "sol_2" / "lecture.xml")
          )
        )
      )
    )
  }

  private val xmlColl1Ex3 = {
    val exResPath = exerciseResourcesPath(toolId, 1, 3)

    InitialExercise(
      title = "Krankenhaus",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument.",
      difficulty = Level.Beginner,
      content = XmlExerciseContent(
        rootNode = "praxis",
        grammarDescription = loadTextFromFile(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_1" / "praxis.dtd"),
            document = loadTextFromFile(exResPath / "sol_1" / "praxis.xml")
          )
        )
      )
    )
  }

  private val xmlColl1Ex4 = {
    val exResPath = exerciseResourcesPath(toolId, 1, 4)

    InitialExercise(
      title = "Frühstück",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie für dieses Xml-Dokument eine passende DTD.",
      difficulty = Level.Beginner,
      content = XmlExerciseContent(
        rootNode = "breakfast",
        grammarDescription = loadTextFromFile(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_1" / "breakfast.dtd"),
            document = loadTextFromFile(exResPath / "sol_1" / "breakfast.xml")
          )
        )
      )
    )
  }

  private val xmlColl1Ex5 = {
    val exResPath = exerciseResourcesPath(toolId, 1, 5)

    InitialExercise(
      title = "Bibliothek",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie für dieses Xml-Dokument eine passende DTD.",
      difficulty = Level.Intermediate,
      content = XmlExerciseContent(
        rootNode = "bibliothek",
        grammarDescription = loadTextFromFile(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_1" / "library.dtd"),
            document = loadTextFromFile(exResPath / "sol_1" / "library.xml")
          ),
          XmlSolution(
            grammar = loadTextFromFile(exResPath / "sol_2" / "library.dtd"),
            document = loadTextFromFile(exResPath / "sol_2" / "library.xml")
          )
        )
      )
    )
  }

  val initialData: InitialData[XmlExerciseContent] = InitialData[XmlExerciseContent](
    initialCollections = Map(
      1 -> InitialCollection(
        title = "Xml Basics",
        initialExercises = Map(
          1 -> xmlColl1Ex1,
          2 -> xmlColl1Ex2,
          3 -> xmlColl1Ex3,
          4 -> xmlColl1Ex4,
          5 -> xmlColl1Ex5
        )
      )
    )
  )

}
