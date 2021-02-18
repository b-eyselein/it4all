package initialData.programming.coll_1

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex4 extends ProgrammingInitialExercise(1, 4, "fibonacci") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Diese Implementierung ist korrekt und sollte alle Tests bestehen.", shouldFail = false),
      unitTestTestConfig(1, "Falls das Argument 'number' keine Zahl ist, soll eine Exception geworfen werden."),
      unitTestTestConfig(2, "Falls das Argument 'number' kleiner als 0 ist, soll eine Exception geworfen werden."),
      unitTestTestConfig(3, "Die Funktion sollte den richtigen Startwert für 0 benutzen."),
      unitTestTestConfig(4, "Die Funktion sollte den richtigen Startwert für 1 benutzen."),
      unitTestTestConfig(5, "Die Funktion sollte das richtige Ergebnis berechnen.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl1Ex4: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Fibonacci",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Recursion, Level.Beginner)
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
