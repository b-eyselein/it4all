package initialData.programming.coll_3

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl3Ex1 extends ProgrammingInitialExerciseContainer(3, 1, "lottery") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Bei Gewinnstufe 0 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(2, "Bei Gewinnstufe 1 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(3, "Bei Gewinnstufe 2 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(4, "Bei Gewinnstufe 3 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(5, "Bei Gewinnstufe 4 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(6, "Bei Gewinnstufe 5 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(7, "Bei einer Gewinnststufe kleiner 0 oder größer 5 soll die Gewinnstufe 0 angenommen werden.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl3Ex1 = InitialExercise(
    title = "Lotterie",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
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
