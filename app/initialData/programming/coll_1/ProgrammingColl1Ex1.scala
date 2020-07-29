package initialData.programming.coll_1

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import play.api.libs.json.{JsNumber, Json}

object ProgrammingColl1Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 1, 1)

  private val unitTestPart = SimplifiedUnitTestPart(
    simplifiedTestMainFile = ExerciseFile(
      name = "test_main.py",
      fileType,
      editable = false,
      content = loadTextFromFile(exResPath / "test_main.py")
    ),
    sampleTestData = Seq(
      ProgTestData(id = 1, input = Json.arr(12, 4), output = JsNumber(4)),
      ProgTestData(id = 2, input = Json.arr(3, 7), output = JsNumber(1)),
      ProgTestData(id = 3, input = Json.arr(64, 46), output = JsNumber(2)),
      ProgTestData(id = 4, input = Json.arr(777, 111), output = JsNumber(111)),
      ProgTestData(id = 5, input = Json.arr(15, 25), output = JsNumber(5))
    )
  )

  private val implementationPart = ImplementationPart(
    base = """def ggt(a: int, b: int) -> int:
             |    return 0
             |""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "ggt.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "ggt_declaration.py")
      )
    ),
    implFileName = "ggt.py",
    sampleSolFileNames = Seq("ggt.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "ggt.py",
            fileType,
            editable = true,
            content = loadTextFromFile(exResPath / "ggt.py")
          )
        )
      )
    )
  )

  val programmingColl1Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId,
    title = "Größter gemeinsamer Teiler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    content = ProgrammingExerciseContent(
      filename = "ggt",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
