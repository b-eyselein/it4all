package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex2 extends ProgrammingInitialExercise(2, 2) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig(
          "floating_point_exponential.py",
          fileType,
          maybeOtherFileName = Some("floating_point_exponential_declaration.py")
        ),
        FileLoadConfig(
          "test_floating_point_exponential.py",
          fileType,
          editable = true,
          Some("test_floating_point_exponential_declaration.py")
        )
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "floating_point_exponential_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "floating_point_exponential_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Mantisse soll alle Dezimalstellen beinhalten.",
        file = ExerciseFile(
          name = "floating_point_exponential_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "floating_point_exponential_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert und nicht multipliziert werden.",
        file = ExerciseFile(
          name = "floating_point_exponential_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "floating_point_exponential_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert werden.",
        file = ExerciseFile(
          name = "floating_point_exponential_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "floating_point_exponential_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Ausgabe soll richtig formatiert sein {Mantisse}e{Exponent}.",
        file = ExerciseFile(
          name = "floating_point_exponential_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "floating_point_exponential_4.py")
        )
      )
    ),
    testFileName = "test_floating_point_exponential.py",
    folderName = "floating_point_exponential",
    sampleSolFileNames = Seq("test_floating_point_exponential.py")
  )

  private val implementationPart = ImplementationPart(
    base = """from math import log10
             |
             |
             |def format_floating_point_exponential(number: float) -> str:
             |    pass""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_floating_point_exponential.py", fileType),
        FileLoadConfig(
          "floating_point_exponential.py",
          fileType,
          editable = true,
          Some("floating_point_exponential_declaration.py")
        )
      )
    ),
    implFileName = "floating_point_exponential.py",
    sampleSolFileNames = Seq("floating_point_exponential.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("floating_point_exponential.py", fileType),
      FileLoadConfig("test_floating_point_exponential.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl2Ex2: ProgrammingExercise = Exercise(
    exerciseId = 2,
    collectionId = 2,
    toolId,
    title = "Fließkommazahl in Exponentialschreibweise",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      filename = "floating_point_exponential",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
