package initialData.xml

import better.files.File
import initialData.InitialData
import initialData.InitialData._
import model.tools.xml.XmlTool.XmlExercise
import model.tools.xml.{XmlExerciseContent, XmlSolution}
import model.{Exercise, ExerciseCollection, SampleSolution}

object XmlData extends InitialData[XmlSolution, XmlExerciseContent] {

  override protected val toolId = "xml"

  private val xmlColl1Ex1: XmlExercise = {
    val exResPath: File = exerciseResourcesPath("toolId", 1, 1)

    Exercise(
      exerciseId = 1,
      collectionId = 1,
      toolId,
      title = "Part",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument",
      difficulty = 1,
      content = XmlExerciseContent(
        rootNode = "part",
        grammarDescription = load_text_from_file(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          SampleSolution(
            id = 1,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_1" / "party.dtd"),
              document = load_text_from_file(exResPath / "sol_1" / "party.xml")
            )
          )
        )
      )
    )
  }

  private val xmlColl1Ex2: XmlExercise = {
    val exResPath: File = exerciseResourcesPath(toolId, 1, 2)

    Exercise(
      exerciseId = 2,
      collectionId = 1,
      toolId,
      title = "Vorlesung",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument.",
      difficulty = 2,
      content = XmlExerciseContent(
        rootNode = "lecture",
        grammarDescription = load_text_from_file(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          SampleSolution(
            id = 1,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_1" / "lecture.dtd"),
              document = load_text_from_file(exResPath / "sol_1" / "lecture.xml")
            )
          ),
          SampleSolution(
            id = 2,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_2" / "lecture.dtd"),
              document = load_text_from_file(exResPath / "sol_2" / "lecture.xml")
            )
          )
        )
      )
    )
  }

  private val xmlColl1Ex3: XmlExercise = {
    val exResPath = exerciseResourcesPath(toolId, 1, 3)

    Exercise(
      exerciseId = 3,
      collectionId = 1,
      toolId,
      title = "Krankenhaus",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie zu dieser DTD ein passendes Xml-Dokument.",
      difficulty = 1,
      content = XmlExerciseContent(
        rootNode = "praxis",
        grammarDescription = load_text_from_file(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          SampleSolution(
            id = 1,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_1" / "praxis.dtd"),
              document = load_text_from_file(exResPath / "sol_1" / "praxis.xml")
            )
          )
        )
      )
    )
  }

  private val xmlColl1Ex4: XmlExercise = {
    val exResPath = exerciseResourcesPath(toolId, 1, 4)

    Exercise(
      exerciseId = 4,
      collectionId = 1,
      toolId,
      title = "Frühstück",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie für dieses Xml-Dokument eine passende DTD.",
      difficulty = 1,
      content = XmlExerciseContent(
        rootNode = "breakfast",
        grammarDescription = load_text_from_file(exResPath / "grammarDescription.txt"),
        sampleSolutions = Seq(
          SampleSolution(
            id = 1,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_1" / "breakfast.dtd"),
              document = load_text_from_file(exResPath / "sol_1" / "breakfast.xml")
            )
          )
        )
      )
    )
  }

  private val xmlColl1Ex5: XmlExercise = {
    val exResPath = exerciseResourcesPath(toolId, 1, 5)

    Exercise(
      exerciseId = 5,
      collectionId = 1,
      toolId = "xml",
      title = "Bibliothek",
      authors = Seq("bje40dc"),
      text = "Erstellen Sie für dieses Xml-Dokument eine passende DTD.",
      difficulty = 2,
      content = XmlExerciseContent(
        rootNode = "bibliothek",
        grammarDescription = load_text_from_file(exResPath / "xml" / "coll_1" / "ex_5" / "grammarDescription.txt"),
        sampleSolutions = Seq(
          SampleSolution(
            id = 1,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_1" / "library.dtd"),
              document = load_text_from_file(exResPath / "sol_1" / "library.xml")
            )
          ),
          SampleSolution(
            id = 2,
            sample = XmlSolution(
              grammar = load_text_from_file(exResPath / "sol_2" / "library.dtd"),
              document = load_text_from_file(exResPath / "sol_2" / "library.xml")
            )
          )
        )
      )
    )

  }

  override val data: Seq[(ExerciseCollection, Seq[XmlExercise])] = Seq(
    (
      ExerciseCollection(1, toolId, title = "Xml Basics", authors = Seq("bje40dc"), text = "TODO"),
      Seq(xmlColl1Ex1, xmlColl1Ex2, xmlColl1Ex3, xmlColl1Ex4, xmlColl1Ex5)
    )
  )

}
