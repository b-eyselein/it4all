package initialData.programming.coll_2

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex2 extends ProgrammingInitialExercise(2, 2, "floating_point_exponential") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Mantisse soll alle Dezimalstellen beinhalten."),
      unitTestTestConfig(
        2,
        "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert und nicht multipliziert werden."
      ),
      unitTestTestConfig(3, "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert werden."),
      unitTestTestConfig(4, "Die Ausgabe soll richtig formatiert sein {Mantisse}e{Exponent}.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Fließkommazahl in Exponentialschreibweise",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
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
