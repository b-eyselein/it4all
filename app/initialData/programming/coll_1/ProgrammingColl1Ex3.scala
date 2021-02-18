package initialData.programming.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex3 extends ProgrammingInitialExercise(1, 3, "babylonian_root") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Diese Implementierung ist korrekt und sollte alle Tests bestehen.", shouldFail = false),
      unitTestTestConfig(
        1,
        "Falls das Funktionsargument 'count' keine Ganzzahl ist, soll eine Exception geworfen werden."
      ),
      unitTestTestConfig(2, "Falls das Argument 'count' kleiner als 0 ist, soll eine Exception geworfen werden."),
      unitTestTestConfig(3, "Falls das Argument 'number' keine Zahl ist, soll eine Exception geworfen werden."),
      unitTestTestConfig(
        4,
        "Falls das Argument 'number' kleiner oder gleich 0 ist, soll eine Exception geworfen werden."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion sollte das korrekte Ergebnis liefern, indem sie die korrekte Anzahl an Iterationen durchführt."
      )
    ),
    testFileName = "test_babylonian_root.py",
    folderName = exerciseBaseName,
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
