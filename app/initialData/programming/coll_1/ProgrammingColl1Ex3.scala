package initialData.programming.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex3 extends ProgrammingInitialExercise(1, 3) {

  private val unitTestTestConfigs = Seq(
    UnitTestTestConfig(
      id = 0,
      shouldFail = false,
      description = "Diese Implementierung ist korrekt und sollte alle Tests bestehen.",
      file = ExerciseFile(
        name = "babylonian_root_0.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_0.py")
      )
    ),
    UnitTestTestConfig(
      id = 1,
      shouldFail = true,
      description = "Falls das Funktionsargument 'count' keine Ganzzahl ist, soll eine Exception geworfen werden.",
      file = ExerciseFile(
        name = "babylonian_root_1.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_1.py")
      )
    ),
    UnitTestTestConfig(
      id = 2,
      shouldFail = true,
      description = "Falls das Argument 'count' kleiner als 0 ist, soll eine Exception geworfen werden.",
      file = ExerciseFile(
        name = "babylonian_root_2.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_2.py")
      )
    ),
    UnitTestTestConfig(
      id = 3,
      shouldFail = true,
      description = "Falls das Argument 'number' keine Zahl ist, soll eine Exception geworfen werden.",
      file = ExerciseFile(
        name = "babylonian_root_3.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_3.py")
      )
    ),
    UnitTestTestConfig(
      id = 4,
      shouldFail = true,
      description = "Falls das Argument 'number' kleiner oder gleich 0 ist, soll eine Exception geworfen werden.",
      file = ExerciseFile(
        name = "babylonian_root_4.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_4.py")
      )
    ),
    UnitTestTestConfig(
      id = 5,
      shouldFail = true,
      description =
        "Die Funktion sollte das korrekte Ergebnis liefern, indem sie die korrekte Anzahl an Iterationen durchführt.",
      file = ExerciseFile(
        name = "babylonian_root_5.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "unit_test_sols" / "babylonian_root_5.py")
      )
    )
  )

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("babylonian_root.py", fileType, editable = false, Some("babylonian_root_declaration.py")),
        FileLoadConfig(
          "test_babylonian_root.py",
          fileType,
          editable = true,
          Some("test_babylonian_root_declaration.py")
        )
      )
    ),
    unitTestTestConfigs,
    testFileName = "test_babylonian_root.py",
    folderName = "babylonian_root",
    sampleSolFileNames = Seq("test_babylonian_root.py")
  )

  private val implementationPart = ImplementationPart(
    base = "",
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_babylonian_root.py", fileType),
        FileLoadConfig("babylonian_root.py", fileType, editable = true, Some("babylonian_root_declaration.py"))
      )
    ),
    implFileName = "babylonian_root.py",
    sampleSolFileNames = Seq("babylonian_root.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("test_babylonian_root.py", fileType),
      FileLoadConfig("babylonian_root.py", fileType)
    )
  )

  val programmingColl1Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Babylonisches Wurzelziehen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    content = ProgrammingExerciseContent(
      filename = "babylonian_root",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
