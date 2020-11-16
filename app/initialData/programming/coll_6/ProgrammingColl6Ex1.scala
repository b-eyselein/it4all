package initialData.programming.coll_6

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl6Ex1 extends ProgrammingInitialExercise(6, 1) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("temperatures.py", fileType, maybeOtherFileName = Some("temperatures_declaration.py")),
        FileLoadConfig("test_temperatures.py", fileType, editable = true, Some("test_temperatures_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "temperatures_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Die Funktion celsius_to_fahrenheit soll die Temperatur korrekt von Celsius in Fahrenheit umrechnen.",
        file = ExerciseFile(
          name = "temperatures_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion celsius_to_fahrenheit soll die Temperatur korrekt von Celsius in Fahrenheit umrechnen.",
        file = ExerciseFile(
          name = "temperatures_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion celsius_to_kelvin soll die Temperatur korrekt von Celsius in Kelvin umrechnen.",
        file = ExerciseFile(
          name = "temperatures_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion celsius_to_kelvin soll die Temperatur korrekt von Celsius in Kelvin umrechnen.",
        file = ExerciseFile(
          name = "temperatures_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Die Funktion fahrenheit_to_celsius soll die Temperatur korrekt von Fahrenheit in Celsius umrechnen.",
        file = ExerciseFile(
          name = "temperatures_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion fahrenheit_to_celsius soll die Temperatur korrekt von Fahrenheit in Celsius umrechnen.",
        file = ExerciseFile(
          name = "temperatures_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description =
          "Die Funktion fahrenheit_to_kelvin soll die Temperatur korrekt von Fahrenheit in Kelvin umrechnen.",
        file = ExerciseFile(
          name = "temperatures_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description =
          "Die Funktion fahrenheit_to_kelvin soll die Temperatur korrekt von Fahrenheit in Kelvin umrechnen.",
        file = ExerciseFile(
          name = "temperatures_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Die Funktion kelvin_to_celsius soll die Temperatur korrekt von Kelvin in Celsius umrechnen.",
        file = ExerciseFile(
          name = "temperatures_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Die Funktion kelvin_to_celsius soll die Temperatur korrekt von Kelvin in Celsius umrechnen.",
        file = ExerciseFile(
          name = "temperatures_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_10.py")
        )
      ),
      UnitTestTestConfig(
        id = 11,
        shouldFail = true,
        description =
          "Die Funktion kelvin_to_fahrenheit soll die Temperatur korrekt von Kelvin in Fahrenheit umrechnen.",
        file = ExerciseFile(
          name = "temperatures_11.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_11.py")
        )
      ),
      UnitTestTestConfig(
        id = 12,
        shouldFail = true,
        description =
          "Die Funktion kelvin_to_fahrenheit soll die Temperatur korrekt von Kelvin in Fahrenheit umrechnen.",
        file = ExerciseFile(
          name = "temperatures_12.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "temperatures_12.py")
        )
      )
    ),
    testFileName = "test_temperatures.py",
    folderName = "temperatures",
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

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("temperatures.py", fileType),
      FileLoadConfig("test_temperatures.py", fileType)
    )
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
      filename = "temperatures",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
