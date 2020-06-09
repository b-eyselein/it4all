package initialData.programming.coll_6

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl6Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 6, 1)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "temperatures.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "temperatures_declaration.py")
      ),
      ExerciseFile(
        name = "test_temperatures.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_temperatures_declaration.py")
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
    sampleSolFileNames = Seq("test_temperatures.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_temperatures.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_temperatures.py")
      ),
      ExerciseFile(
        name = "temperatures.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "temperatures_declaration.py")
      )
    ),
    implFileName = "temperatures.py",
    sampleSolFileNames = Seq("temperatures.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "temperatures.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "temperatures.py")
          ),
          ExerciseFile(
            name = "test_temperatures.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_temperatures.py")
          )
        )
      )
    )
  )

  val programmingColl6Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 6,
    toolId,
    title = "Temperaturen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      functionName = "temperatures",
      foldername = "temperatures",
      filename = "temperatures",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}