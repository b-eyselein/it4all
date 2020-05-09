package initialData.programming.coll_4

import initialData.InitialData._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import model._
import play.api.libs.json.{JsNull, JsString, Json}

object ProgrammingColl4Ex2 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 4, 2)

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "longest_string.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "longest_string.py")
          )
        )
      )
    )
  )

  private val sampleTestData = Seq(
    ProgTestData(id = 1, input = Json.arr(), output = JsNull),
    ProgTestData(id = 2, input = Json.arr("0"), output = JsString("0")),
    ProgTestData(id = 3, input = Json.arr("1", "11", "111"), output = JsString("111")),
    ProgTestData(id = 4, input = Json.arr("1", "121", "12321", "232", "3"), output = JsString("12321"))
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import List
             |
             |def longest_string(my_list: List[str]) -> str:
             |    return ''
  """.stripMargin,
    files = Seq(
      ExerciseFile(
        name = "longest_string.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "longest_string_declaration.py")
      )
    ),
    implFileName = "longest_string.py",
    sampleSolFileNames = Seq("longest_string.py")
  )

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Simplified,
    unitTestsDescription = "",
    unitTestFiles = Seq.empty,
    unitTestTestConfigs = Seq.empty,
    testFileName = "test_longest_string.py",
    sampleSolFileNames = Seq.empty
  )

  val programmingColl4Ex2: ProgrammingExercise = Exercise(
    exerciseId = 2,
    collectionId = 4,
    toolId,
    title = "Längste Zeichenkette",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      functionName = "longest_string",
      foldername = "longest_string",
      filename = "longest_string",
      inputTypes = Seq(
        ProgInput(
          id = 1,
          inputName = "my_list",
          inputType = ProgDataTypes.LIST(ProgDataTypes.NonGenericProgDataType.STRING)
        )
      ),
      outputType = ProgDataTypes.NonGenericProgDataType.STRING,
      unitTestPart,
      implementationPart,
      sampleTestData,
      sampleSolutions
    )
  )

}
