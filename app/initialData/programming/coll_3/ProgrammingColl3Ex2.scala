package initialData.programming.coll_3

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl3Ex2 extends ProgrammingInitialExerciseContainer(3, 2, "discount") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Falls der Kunde einen Hund besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(2, "Falls der Kunde eine Katze besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(
        3,
        "Falls der Kunde einen Hund und eine Katze besitzt, soll der korrekte Rabatt gewährt werden."
      ),
      unitTestTestConfig(
        4,
        "Falls der Kunde keinen Hund und keine Katze besitzt, soll der korrekte Rabatt gewährt werden."
      ),
      unitTestTestConfig(5, "Falls der Kunde einen Hamster besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(6, "Falls der Kunde keinen Hamster besitzt, soll der korrekte Rabatt gewährt werden.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl3Ex2 = InitialExercise(
    title = "Rabatt",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
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
