package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex1 extends ProgrammingInitialExercise(5, 1) {

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("tuples.py", fileType),
      FileLoadConfig("test_tuples.py", fileType)
    )
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_tuples.py", fileType),
        FileLoadConfig("tuples.py", fileType, editable = true, Some("tuples_declaration.py"))
      )
    ),
    implFileName = "tuples.py",
    sampleSolFileNames = Seq("tuples.py")
  )

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("tuples.py", fileType, maybeOtherFileName = Some("tuples_declaration.py")),
        FileLoadConfig("test_tuples.py", fileType, editable = true, Some("test_tuples_declaration.py"))
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
    exerciseId,
    collectionId,
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
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
