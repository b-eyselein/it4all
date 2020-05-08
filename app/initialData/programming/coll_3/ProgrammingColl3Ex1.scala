package initialData.programming.coll_3

import model.{Exercise, ExerciseFile, Level, SampleSolution, TopicWithLevel}
import initialData.InitialData._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming.{
  ImplementationPart,
  ProgDataTypes,
  ProgSolution,
  ProgrammingExerciseContent,
  ProgrammingTopics,
  UnitTestPart,
  UnitTestTestConfig,
  UnitTestType
}

object ProgrammingColl3Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 3, 1)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "lottery.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "lottery_declaration.py")
      ),
      ExerciseFile(
        name = "test_lottery.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_lottery_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "lottery_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Bei Gewinnstufe 0 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Bei Gewinnstufe 1 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Bei Gewinnstufe 2 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Bei Gewinnstufe 3 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Bei Gewinnstufe 4 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Bei Gewinnstufe 5 soll der korrekte Gewinn zurückgegeben werden.",
        file = ExerciseFile(
          name = "lottery_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Bei einer Gewinnststufe kleiner 0 oder größer 5 soll die Gewinnstufe 0 angenommen werden.",
        file = ExerciseFile(
          name = "lottery_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "lottery_7.py")
        )
      )
    ),
    testFileName = "test_lottery.py",
    sampleSolFileNames = Seq("test_lottery.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def calculate_lottery_win(pot: float, win_class: int) > float:
             |    pass
             |""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "test_lottery.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_lottery.py")
      ),
      ExerciseFile(
        name = "lottery.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "lottery_declaration.py")
      )
    ),
    implFileName = "lottery.py",
    sampleSolFileNames = Seq("lottery.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "lottery.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "lottery.py")
          ),
          ExerciseFile(
            name = "test_lottery.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_lottery.py")
          )
        )
      )
    )
  )

  val programmingColl3Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 3,
    toolId,
    title = "Lotterie",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      functionName = "lottery",
      foldername = "lottery",
      filename = "lottery",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}
