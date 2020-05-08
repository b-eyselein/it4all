package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import play.api.libs.json.{JsBoolean, JsString}

object ProgrammingColl2Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 1)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Simplified,
    unitTestsDescription = "",
    unitTestFiles = Seq.empty,
    unitTestTestConfigs = Seq.empty,
    testFileName = "test_palindrome.py",
    sampleSolFileNames = Seq.empty
  )

  private val implementationPart = ImplementationPart(
    base = """def is_palindrome(word: str) -> bool:
             |    return False """.stripMargin,
    files = Seq(
      ExerciseFile(
        name = "palindrome.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "palindrome_declaration.py")
      )
    ),
    implFileName = "palindrome.py",
    sampleSolFileNames = Seq("palindrome.py")
  )

  private val sampleTestData = Seq(
    ProgTestData(id = 1, input = JsString("anna"), output = JsBoolean(true)),
    ProgTestData(id = 2, input = JsString("ananas"), output = JsBoolean(false)),
    ProgTestData(id = 3, input = JsString(""), output = JsBoolean(true)),
    ProgTestData(id = 4, input = JsString("qwertzuiiuztrewq"), output = JsBoolean(true)),
    ProgTestData(id = 5, input = JsString("qwertzuiyiuztrewq"), output = JsBoolean(true)),
    ProgTestData(id = 6, input = JsString("Tacocat"), output = JsBoolean(true))
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "palindrome.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "palindrome.py")
          )
        )
      )
    )
  )

  val programmingColl2Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 2,
    toolId,
    title = "Palindrom",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      functionName = "is_palindrome",
      foldername = "palindrome",
      filename = "palindrome",
      inputTypes = Seq(
        ProgInput(id = 1, inputName = "word", inputType = ProgDataTypes.NonGenericProgDataType.STRING)
      ),
      outputType = ProgDataTypes.NonGenericProgDataType.BOOLEAN,
      unitTestPart,
      implementationPart,
      sampleTestData,
      sampleSolutions
    )
  )

}
