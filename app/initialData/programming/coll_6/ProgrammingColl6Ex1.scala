package initialData.programming.coll_6

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl6Ex1 extends ProgrammingInitialExerciseContainer(6, 1, "temperatures") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion celsius_to_fahrenheit soll die Temperatur korrekt von Celsius in Fahrenheit umrechnen."
      ),
      unitTestTestConfig(
        2,
        "Die Funktion celsius_to_fahrenheit soll die Temperatur korrekt von Celsius in Fahrenheit umrechnen."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion celsius_to_kelvin soll die Temperatur korrekt von Celsius in Kelvin umrechnen."
      ),
      unitTestTestConfig(
        4,
        "Die Funktion celsius_to_kelvin soll die Temperatur korrekt von Celsius in Kelvin umrechnen."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion fahrenheit_to_celsius soll die Temperatur korrekt von Fahrenheit in Celsius umrechnen."
      ),
      unitTestTestConfig(
        6,
        "Die Funktion fahrenheit_to_celsius soll die Temperatur korrekt von Fahrenheit in Celsius umrechnen."
      ),
      unitTestTestConfig(
        7,
        "Die Funktion fahrenheit_to_kelvin soll die Temperatur korrekt von Fahrenheit in Kelvin umrechnen."
      ),
      unitTestTestConfig(
        8,
        "Die Funktion fahrenheit_to_kelvin soll die Temperatur korrekt von Fahrenheit in Kelvin umrechnen."
      ),
      unitTestTestConfig(
        9,
        "Die Funktion kelvin_to_celsius soll die Temperatur korrekt von Kelvin in Celsius umrechnen."
      ),
      unitTestTestConfig(
        10,
        "Die Funktion kelvin_to_celsius soll die Temperatur korrekt von Kelvin in Celsius umrechnen."
      ),
      unitTestTestConfig(
        11,
        "Die Funktion kelvin_to_fahrenheit soll die Temperatur korrekt von Kelvin in Fahrenheit umrechnen."
      ),
      unitTestTestConfig(
        12,
        "Die Funktion kelvin_to_fahrenheit soll die Temperatur korrekt von Kelvin in Fahrenheit umrechnen."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl6Ex1 = InitialExercise(
    title = "Temperaturen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
