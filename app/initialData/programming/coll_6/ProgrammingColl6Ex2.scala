package initialData.programming.coll_6

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl6Ex2 extends ProgrammingInitialExercise(6, 2) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("distances.py", fileType, maybeOtherFileName = Some("distances_declaration.py")),
        FileLoadConfig("test_distances.py", fileType, editable = true, Some("test_distances_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "distances_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Funktion yards_to_meters soll die Distanz korrekt von Yards in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Die Funktion yards_to_meters soll die Distanz korrekt von Yards in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion meters_to_yards soll die Distanz korrekt von Meter in Yards umrechnen.",
        file = ExerciseFile(
          name = "distances_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion meters_to_yards soll die Distanz korrekt von Meter in Yards umrechnen.",
        file = ExerciseFile(
          name = "distances_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion miles_to_meters soll die Distanz korrekt von Meilen in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Die Funktion miles_to_meters soll die Distanz korrekt von Meilen in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Die Funktion meters_to_miles soll die Distanz korrekt von Meter in Meilen umrechnen.",
        file = ExerciseFile(
          name = "distances_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Die Funktion meters_to_miles soll die Distanz korrekt von Meter in Meilen umrechnen.",
        file = ExerciseFile(
          name = "distances_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Die Funktion seamiles_to_meters soll die Distanz korrekt von Seemeilen in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Die Funktion seamiles_to_meters soll die Distanz korrekt von Seemeilen in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_10.py")
        )
      ),
      UnitTestTestConfig(
        id = 11,
        shouldFail = true,
        description = "Die Funktion meters_to_seamiles soll die Distanz korrekt von Meter in Seemeilen umrechnen.",
        file = ExerciseFile(
          name = "distances_11.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_11.py")
        )
      ),
      UnitTestTestConfig(
        id = 12,
        shouldFail = true,
        description = "Die Funktion meters_to_seamiles soll die Distanz korrekt von Meter in Seemeilen umrechnen.",
        file = ExerciseFile(
          name = "distances_12.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_12.py")
        )
      ),
      UnitTestTestConfig(
        id = 13,
        shouldFail = true,
        description = "Die Funktion inches_to_meters soll die Distanz korrekt von Zoll in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_13.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_13.py")
        )
      ),
      UnitTestTestConfig(
        id = 14,
        shouldFail = true,
        description = "Die Funktion inches_to_meters soll die Distanz korrekt von Zoll in Meter umrechnen.",
        file = ExerciseFile(
          name = "distances_14.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_14.py")
        )
      ),
      UnitTestTestConfig(
        id = 15,
        shouldFail = true,
        description = "Die Funktion meters_to_inches soll die Distanz korrekt von Meter in Zoll umrechnen.",
        file = ExerciseFile(
          name = "distances_15.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_15.py")
        )
      ),
      UnitTestTestConfig(
        id = 16,
        shouldFail = true,
        description = "Die Funktion meters_to_inches soll die Distanz korrekt von Meter in Zoll umrechnen.",
        file = ExerciseFile(
          name = "distances_16.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "distances_16.py")
        )
      )
    ),
    testFileName = "test_distances.py",
    folderName = "distances",
    sampleSolFileNames = Seq("test_distances.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_distances.py", fileType),
        FileLoadConfig("distances.py", fileType, editable = true, Some("distances_declaration.py"))
      )
    ),
    implFileName = "distances.py",
    sampleSolFileNames = Seq("distances.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("distances.py", fileType),
      FileLoadConfig("test_distances.py", fileType)
    )
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
      filename = "distances",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
