package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex1 extends ProgrammingInitialExercise(5, 1, "tuples") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(id = 0, description = "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion min_max soll für Listen mit einem Element das korrekte Minimum und Maximum zurückgeben."
      ),
      unitTestTestConfig(
        2,
        "Die Funktion min_max soll für Listen mit mehr als einem Element das korrekte Minimum und Maximum zurückgeben."
      ),
      unitTestTestConfig(3, "Die Funktion account_value soll den korrekten Gesamtwert aller Aktien berechnen."),
      unitTestTestConfig(4, "Die Funktion account_value soll den Gesamtwert in Euro berechnen."),
      unitTestTestConfig(5, "Die Funktion account_value soll 0 zurückgeben, falls die übergebene Liste leer ist."),
      unitTestTestConfig(
        6,
        "Falls eine Aktie mit dem übergebenen Namen in der übergebenen Liste vorhanden ist, soll die Funktion stock_value den korrekten Wert der Aktie zurückgeben."
      ),
      unitTestTestConfig(
        7,
        "Falls keine Aktie mit dem übergebenen Namen in der übergebenen Liste vorhanden ist, soll die Funktion stock_value -1 zurückgeben."
      ),
      unitTestTestConfig(8, "Falls die übergebene Liste leer ist, soll die Funktion stock_value -1 zurückgeben.")
    ),
    testFileName = "test_tuples.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_tuples.py")
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
