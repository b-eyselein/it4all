package initialData.programming.coll_3

import initialData.InitialData._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import model._

object ProgrammingColl3Ex3 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 3, 3)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "greet.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "greet_declaration.py")
      ),
      ExerciseFile(
        name = "test_greet.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_greet_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "greet_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Für die Zeit von 0 bis 6 Uhr soll die korrekte Grußformel zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Für die Zeit von 6 bis 12 Uhr soll die korrekte Grußformel zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Für die Zeit von 12 bis 18 Uhr soll die korrekte Grußformel zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Für die Zeit von 18 bis 21 Uhr soll die korrekte Grußformel zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Für die Zeit von 21 bis 24 Uhr soll die korrekte Grußformel zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Für eine ungültige Zeit kleiner als 0 soll 'I do not know this time' zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Für eine ungültige Zeit größer als 24 soll 'I do not know this time' zurückgegeben werden.",
        file = ExerciseFile(
          name = "greet_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "greet_7.py")
        )
      )
    ),
    testFileName = "test_greet.py",
    sampleSolFileNames = Seq("test_greet.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def greet(hour: int) -> str:
             |    pass""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "test_greet.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_greet.py")
      ),
      ExerciseFile(
        name = "greet.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "greet_declaration.py")
      )
    ),
    implFileName = "greet.py",
    sampleSolFileNames = Seq("greet.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "greet.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "greet.py")
          ),
          ExerciseFile(
            name = "test_greet.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_greet.py")
          )
        )
      )
    )
  )

  val programmingColl3Ex3: ProgrammingExercise = Exercise(
    exerciseId = 3,
    collectionId = 3,
    toolId,
    title = "Grußformel",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      functionName = "greet",
      foldername = "greet",
      filename = "greet",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}
