package initialData.programming.coll_7

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl7Ex3 extends ProgrammingInitialExercise(7, 3) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "TODO!",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("vector2d.py", fileType, maybeOtherFileName = Some("vector2d_declaration.py")),
        FileLoadConfig("test_vector2d.py", fileType, editable = true, Some("test_vector2d_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "vector2d_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Der Wert des Konstruktorarguments 'x' sollte unter dem selben Namen als Argument zugänglich sein.",
        file = ExerciseFile(
          name = "vector2d_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Der Wert des Konstruktorarguments 'y' sollte unter dem selben Namen als Argument zugänglich sein.",
        file = ExerciseFile(
          name = "vector2d_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Die Funktion __repr__ soll die korrekte Koordinatenrepräsentation der Koordinate 'x' zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description =
          "Die Funktion __repr__ soll die korrekte Koordinatenrepräsentation der Koordinate 'y' zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls sich zwei Vektoren gleichen.",
        file = ExerciseFile(
          name = "vector2d_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls sich zwei Vektoren nicht gleichen.",
        file = ExerciseFile(
          name = "vector2d_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description =
          "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls der Vektor2D mit einer Instanz verglichen wird, die kein Vektor2D ist.",
        file = ExerciseFile(
          name = "vector2d_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Die Funktion abs soll die korrekte Länge berechnen.",
        file = ExerciseFile(
          name = "vector2d_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Die Funktion abs soll die korrekte Länge berechnen.",
        file = ExerciseFile(
          name = "vector2d_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Die Funktion __add__ soll jeweils die x- und y-Koordinaten addieren.",
        file = ExerciseFile(
          name = "vector2d_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_10.py")
        )
      ),
      UnitTestTestConfig(
        id = 11,
        shouldFail = true,
        description = "Die Funktion __add__ soll den korrekt berechneten Vektor nach der Addition zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_11.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_11.py")
        )
      ),
      UnitTestTestConfig(
        id = 12,
        shouldFail = true,
        description = "Die Funktion __sub__ soll jeweils die x- und y-Koordinaten subtrahieren.",
        file = ExerciseFile(
          name = "vector2d_12.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_12.py")
        )
      ),
      UnitTestTestConfig(
        id = 13,
        shouldFail = true,
        description = "Die Funktion __sub__ soll den korrekt berechneten Vektor nach der Subtraktion zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_13.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_13.py")
        )
      ),
      UnitTestTestConfig(
        id = 14,
        shouldFail = true,
        description =
          "Die Funktion __mul__ soll jeweils die x- und y-Koordinaten mit dem übergebenen Wert multiplizieren.",
        file = ExerciseFile(
          name = "vector2d_14.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_14.py")
        )
      ),
      UnitTestTestConfig(
        id = 15,
        shouldFail = true,
        description = "Die Funktion __mul__ soll den korrekt berechneten Vektor nach der Multiplikation zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_15.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_15.py")
        )
      ),
      UnitTestTestConfig(
        id = 16,
        shouldFail = true,
        description =
          "Die Funktion dot soll jeweils die x- und y-Koordinaten multiplizieren und anschließend das Ergebnis addieren.",
        file = ExerciseFile(
          name = "vector2d_16.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_16.py")
        )
      ),
      UnitTestTestConfig(
        id = 17,
        shouldFail = true,
        description = "Die Funktion dot soll das korrekt berechnete Skalarprodukt zurückgeben.",
        file = ExerciseFile(
          name = "vector2d_17.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "vector2d_17.py")
        )
      )
    ),
    testFileName = "test_vector2d.py",
    folderName = "vector2d",
    sampleSolFileNames = Seq("test_vector2d.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_vector2d.py", fileType),
        FileLoadConfig("vector2d.py", fileType, editable = true, Some("vector2d_declaration.py"))
      )
    ),
    implFileName = "vector2d.py",
    sampleSolFileNames = Seq("vector2d.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("vector2d.py", fileType),
      FileLoadConfig("test_vector2d.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl7Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Vektor2D",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Classes, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "vector2d",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
