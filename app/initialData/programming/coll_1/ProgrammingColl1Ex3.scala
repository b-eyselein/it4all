package initialData.programming.coll_1

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.{ProgrammingInitialData, ProgrammingInitialExerciseContainer}
import model._
import model.tools.programming._

object ProgrammingColl1Ex3 extends ProgrammingInitialExerciseContainer(1, 3, "babylonian_root") {

  private val unitTestPart = UnitTestPart(
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
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl1Ex3: ProgrammingInitialData.InitialEx = InitialExercise(
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
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
