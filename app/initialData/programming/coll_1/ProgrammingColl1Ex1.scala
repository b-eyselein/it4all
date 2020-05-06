package initialData.programming.coll_1

import initialData.InitialData._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import model._
import play.api.libs.json.{JsNumber, Json}

object ProgrammingColl1Ex1 {

  private val ex_res_path = exerciseResourcesPath("programming", 1, 1)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Simplified,
    unitTestsDescription = "TODO!",
    unitTestFiles = Seq.empty,
    unitTestTestConfigs = Seq.empty,
    simplifiedTestMainFile = Some(
      ExerciseFile(
        name = "test_main.py",
        fileType = "python",
        editable = false,
        content = loadTextFromFile(ex_res_path / "test_main.py")
      )
    ),
    testFileName = "test_ggt.py",
    sampleSolFileNames = Seq.empty
  )

  private val implementationPart = ImplementationPart(
    base = """def ggt(a: int, b: int) -> int:
             |    return 0
             |""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "ggt.py",
        fileType = "python",
        editable = true,
        content = loadTextFromFile(ex_res_path / "ggt_declaration.py")
      )
    ),
    implFileName = "ggt.py",
    sampleSolFileNames = Seq("ggt.py")
  )

  private val sampleSolution: SampleSolution[ProgSolution] = SampleSolution(
    id = 1,
    sample = ProgSolution(
      files = Seq(
        ExerciseFile(
          name = "ggt.py",
          fileType = "python",
          editable = true,
          content = loadTextFromFile(ex_res_path / "ggt.py")
        )
      ) /*,
      testData = Seq.empty
     */
    )
  )

  val programmingColl1Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "programming",
    title = "Größter gemeinsamer Teiler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(ex_res_path / "text.html"),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    content = ProgrammingExerciseContent(
      functionName = "ggt",
      foldername = "ggt",
      filename = "ggt",
      inputTypes = Seq(
        ProgInput(id = 1, inputName = "a", inputType = ProgDataTypes.NonGenericProgDataType.INTEGER),
        ProgInput(id = 2, inputName = "b", inputType = ProgDataTypes.NonGenericProgDataType.INTEGER)
      ),
      outputType = ProgDataTypes.NonGenericProgDataType.INTEGER,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq(
        ProgTestData(id = 1, input = Json.arr(12, 4), output = JsNumber(4)),
        ProgTestData(id = 2, input = Json.arr(3, 7), output = JsNumber(1)),
        ProgTestData(id = 3, input = Json.arr(64, 46), output = JsNumber(2)),
        ProgTestData(id = 4, input = Json.arr(777, 111), output = JsNumber(111)),
        ProgTestData(id = 5, input = Json.arr(15, 25), output = JsNumber(5))
      ),
      sampleSolutions = Seq(sampleSolution),
      baseData = None
    )
  )

}
