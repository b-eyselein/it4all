package initialData.programming.coll_4

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import play.api.libs.json.{JsArray, JsNull, JsNumber, Json}

object ProgrammingColl4Ex1 extends ProgrammingInitialExercise(4, 1, "average") {

  private val unitTestPart = SimplifiedUnitTestPart(
    simplifiedTestMainFile = ExerciseFile(
      name = "test_main.py",
      fileType,
      editable = false,
      content = loadTextFromFile(exResPath / "test_main.py")
    ),
    sampleTestData = Seq(
      ProgTestData(id = 1, input = JsArray(), output = JsNull),
      ProgTestData(id = 2, input = Json.arr(1), output = JsNumber(1.0)),
      ProgTestData(id = 3, input = Json.arr(3, 5, 7), output = JsNumber(5.0))
    )
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import List
             |
             |def average(my_list: List[int]) -> float:
             |    return 0""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("average.py", fileType, editable = true, Some("average_declaration.py"))
      )
    ),
    implFileName = "average.py",
    sampleSolFileNames = Seq("average.py")
  )

  override protected val sampleSolutionFiles =
    loadFilesFromFolder(exResPath, Seq(FileLoadConfig("average.py", fileType)))

  val programmingColl4Ex1: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Durchschnitt",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
