package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex2 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 2)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "floating_point_exponential.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "floating_point_exponential_declaration.py")
      ),
      ExerciseFile(
        name = "test_floating_point_exponential.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_floating_point_exponential_declaration.py")
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
    files = Seq(
      ExerciseFile(
        name = "test_floating_point_exponential.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_floating_point_exponential.py")
      ),
      ExerciseFile(
        name = "floating_point_exponential.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "floating_point_exponential_declaration.py")
      )
    ),
    implFileName = "floating_point_exponential.py",
    sampleSolFileNames = Seq("floating_point_exponential.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = FilesSolution(
        files = Seq(
          ExerciseFile(
            name = "floating_point_exponential.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "floating_point_exponential.py")
          ),
          ExerciseFile(
            name = "test_floating_point_exponential.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_floating_point_exponential.py")
          )
        )
      )
    )
  )

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
