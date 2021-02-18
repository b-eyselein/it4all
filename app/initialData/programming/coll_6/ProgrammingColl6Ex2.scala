package initialData.programming.coll_6

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl6Ex2 extends ProgrammingInitialExercise(6, 2, "distances") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Funktion yards_to_meters soll die Distanz korrekt von Yards in Meter umrechnen."),
      unitTestTestConfig(2, "Die Funktion yards_to_meters soll die Distanz korrekt von Yards in Meter umrechnen."),
      unitTestTestConfig(3, "Die Funktion meters_to_yards soll die Distanz korrekt von Meter in Yards umrechnen."),
      unitTestTestConfig(4, "Die Funktion meters_to_yards soll die Distanz korrekt von Meter in Yards umrechnen."),
      unitTestTestConfig(5, "Die Funktion miles_to_meters soll die Distanz korrekt von Meilen in Meter umrechnen."),
      unitTestTestConfig(6, "Die Funktion miles_to_meters soll die Distanz korrekt von Meilen in Meter umrechnen."),
      unitTestTestConfig(7, "Die Funktion meters_to_miles soll die Distanz korrekt von Meter in Meilen umrechnen."),
      unitTestTestConfig(8, "Die Funktion meters_to_miles soll die Distanz korrekt von Meter in Meilen umrechnen."),
      unitTestTestConfig(
        9,
        "Die Funktion seamiles_to_meters soll die Distanz korrekt von Seemeilen in Meter umrechnen."
      ),
      unitTestTestConfig(
        10,
        "Die Funktion seamiles_to_meters soll die Distanz korrekt von Seemeilen in Meter umrechnen."
      ),
      unitTestTestConfig(
        11,
        "Die Funktion meters_to_seamiles soll die Distanz korrekt von Meter in Seemeilen umrechnen."
      ),
      unitTestTestConfig(
        12,
        "Die Funktion meters_to_seamiles soll die Distanz korrekt von Meter in Seemeilen umrechnen."
      ),
      unitTestTestConfig(13, "Die Funktion inches_to_meters soll die Distanz korrekt von Zoll in Meter umrechnen."),
      unitTestTestConfig(14, "Die Funktion inches_to_meters soll die Distanz korrekt von Zoll in Meter umrechnen."),
      unitTestTestConfig(15, "Die Funktion meters_to_inches soll die Distanz korrekt von Meter in Zoll umrechnen."),
      unitTestTestConfig(16, "Die Funktion meters_to_inches soll die Distanz korrekt von Meter in Zoll umrechnen.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl6Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Längen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
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
