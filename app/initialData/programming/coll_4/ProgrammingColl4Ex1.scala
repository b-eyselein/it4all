package initialData.programming.coll_4

import initialData.InitialData._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import model._
import play.api.libs.json.{JsArray, JsNull, JsNumber, Json}

object ProgrammingColl4Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 4, 1)

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "average.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "average.py")
          )
        )
      )
    )
  )

  private val sampleTestData = Seq(
    ProgTestData(id = 1, input = JsArray(), output = JsNull),
    ProgTestData(id = 2, input = Json.arr(1), output = JsNumber(1.0)),
    ProgTestData(id = 3, input = Json.arr(3, 5, 7), output = JsNumber(5.0))
  )

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Simplified,
    unitTestsDescription = "",
    unitTestFiles = Seq.empty,
    unitTestTestConfigs = Seq.empty,
    testFileName = "test_average.py",
    sampleSolFileNames = Seq.empty
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import List
             |
             |def average(my_list: List[int]) -> float:
             |    return 0""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "average.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "average_declaration.py")
      )
    ),
    implFileName = "average.py",
    sampleSolFileNames = Seq("average.py")
  )

  val programmingColl4Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 4,
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
      functionName = "average",
      foldername = "average",
      filename = "average",
      inputTypes = Seq(
        ProgInput(
          id = 1,
          inputName = "my_list",
          inputType = ProgDataTypes.LIST(ProgDataTypes.NonGenericProgDataType.INTEGER)
        )
      ),
      outputType = ProgDataTypes.NonGenericProgDataType.FLOAT,
      unitTestPart,
      implementationPart,
      sampleTestData,
      sampleSolutions
    )
  )

}
