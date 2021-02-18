package initialData.programming.coll_6

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl6Ex1 extends ProgrammingInitialExercise(6, 1, "temperatures") {

  private val unitTestPart = NormalUnitTestPart(
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
    testFileName = "test_temperatures.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_temperatures.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_temperatures.py", fileType),
        FileLoadConfig("temperatures.py", fileType, editable = true, Some("temperatures_declaration.py"))
      )
    ),
    implFileName = "temperatures.py",
    sampleSolFileNames = Seq("temperatures.py")
  )

  val programmingColl6Ex1: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
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
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
