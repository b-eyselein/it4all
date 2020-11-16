package initialData.programming.coll_4

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import play.api.libs.json.{JsNull, JsString, Json}

object ProgrammingColl4Ex2 extends ProgrammingInitialExercise(4, 2) {

  private val sampleSolutionFiles = loadFilesFromFolder(exResPath, Seq(FileLoadConfig("longest_string.py", fileType)))

  private val implementationPart = ImplementationPart(
    base = """from typing import List
             |
             |def longest_string(my_list: List[str]) -> str:
             |    return ''
  """.stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig(name = "longest_string.py", fileType, editable = true, Some("longest_string_declaration.py"))
      )
    ),
    implFileName = "longest_string.py",
    sampleSolFileNames = Seq("longest_string.py")
  )

  private val unitTestPart = SimplifiedUnitTestPart(
    simplifiedTestMainFile = ExerciseFile(
      name = "test_main.py",
      fileType,
      editable = false,
      content = loadTextFromFile(exResPath / "test_main.py")
    ),
    sampleTestData = Seq(
      ProgTestData(id = 1, input = Json.arr(), output = JsNull),
      ProgTestData(id = 2, input = Json.arr("0"), output = JsString("0")),
      ProgTestData(id = 3, input = Json.arr("1", "11", "111"), output = JsString("111")),
      ProgTestData(id = 4, input = Json.arr("1", "121", "12321", "232", "3"), output = JsString("12321"))
    )
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
      filename = "longest_string",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
