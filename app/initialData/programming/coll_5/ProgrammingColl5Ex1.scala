package initialData.programming.coll_5

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl5Ex1 extends ProgrammingInitialExerciseContainer(5, 1, "tuples") {

  private val unitTestPart = UnitTestPart(
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
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl5Ex1: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Tupel",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Tuples     -> Level.Beginner,
      ProgrammingTopics.Lists      -> Level.Beginner,
      ProgrammingTopics.Maths      -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
