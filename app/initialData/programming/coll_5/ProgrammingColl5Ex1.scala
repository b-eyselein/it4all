package initialData.programming.coll_5

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 5, 1)

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "tuples.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "tuples.py")
          ),
          ExerciseFile(
            name = "test_tuples.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_tuples.py")
          )
        )
      )
    )
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_tuples.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_tuples.py")
      ),
      ExerciseFile(
        name = "tuples.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "tuples_declaration.py")
      )
    ),
    implFileName = "tuples.py",
    sampleSolFileNames = Seq("tuples.py")
  )

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "tuples.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "tuples_declaration.py")
      ),
      ExerciseFile(
        name = "test_tuples.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_tuples_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "tuples_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Die Funktion min_max soll für Listen mit einem Element das korrekte Minimum und Maximum zurückgeben.",
        file = ExerciseFile(
          name = "tuples_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion min_max soll für Listen mit mehr als einem Element das korrekte Minimum und Maximum zurückgeben.",
        file = ExerciseFile(
          name = "tuples_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion account_value soll den korrekten Gesamtwert aller Aktien berechnen.",
        file = ExerciseFile(
          name = "tuples_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion account_value soll den Gesamtwert in Euro berechnen.",
        file = ExerciseFile(
          name = "tuples_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion account_value soll 0 zurückgeben, falls die übergebene Liste leer ist.",
        file = ExerciseFile(
          name = "tuples_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Falls eine Aktie mit dem übergebenen Namen in der übergebenen Liste vorhanden ist, soll die Funktion stock_value den korrekten Wert der Aktie zurückgeben.",
        file = ExerciseFile(
          name = "tuples_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description =
          "Falls keine Aktie mit dem übergebenen Namen in der übergebenen Liste vorhanden ist, soll die Funktion stock_value -1 zurückgeben.",
        file = ExerciseFile(
          name = "tuples_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Falls die übergebene Liste leer ist, soll die Funktion stock_value -1 zurückgeben.",
        file = ExerciseFile(
          name = "tuples_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_8.py")
        )
      )
    ),
    testFileName = "test_tuples.py",
    folderName = "tuples",
    sampleSolFileNames = Seq("test_tuples.py")
  )

  val programmingColl5Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 5,
    toolId,
    title = "Tupel",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Tuples, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "tuples",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
