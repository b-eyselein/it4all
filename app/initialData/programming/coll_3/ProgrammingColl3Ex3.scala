package initialData.programming.coll_3

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl3Ex3 extends ProgrammingInitialExercise(3, 3, "greet") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description"
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Für die Zeit von 0 bis 6 Uhr soll die korrekte Grußformel zurückgegeben werden."),
      unitTestTestConfig(2, "Für die Zeit von 6 bis 12 Uhr soll die korrekte Grußformel zurückgegeben werden."),
      unitTestTestConfig(3, "Für die Zeit von 12 bis 18 Uhr soll die korrekte Grußformel zurückgegeben werden."),
      unitTestTestConfig(4, "Für die Zeit von 18 bis 21 Uhr soll die korrekte Grußformel zurückgegeben werden."),
      unitTestTestConfig(5, "Für die Zeit von 21 bis 24 Uhr soll die korrekte Grußformel zurückgegeben werden."),
      unitTestTestConfig(
        6,
        "Für eine ungültige Zeit kleiner als 0 soll 'I do not know this time' zurückgegeben werden."
      ),
      unitTestTestConfig(
        7,
        "Für eine ungültige Zeit größer als 24 soll 'I do not know this time' zurückgegeben werden."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl3Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Grußformel",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
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
