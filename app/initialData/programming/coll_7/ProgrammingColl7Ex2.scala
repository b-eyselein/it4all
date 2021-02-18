package initialData.programming.coll_7

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl7Ex2 extends ProgrammingInitialExercise(7, 2, "maumau") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion can_be_played_on soll ebenfalls true zurückgeben, falls die Bilder der beiden Karten übereinstimmen."
      ),
      unitTestTestConfig(
        2,
        "Die Funktion can_be_played_on soll ebenfalls true zurückgeben, falls die Farben der beiden Karten übereinstimmen."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion can_be_played_on soll nur true zurückgeben, falls die Farben der beiden Karten übereinstimmen."
      ),
      unitTestTestConfig(
        4,
        "Die Funktion can_be_played_on soll nur true zurückgeben, falls die Bilder der beiden Karten übereinstimmen."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion can_be_palyed_on soll true zurückgeben, falls entweder die Farben oder die Bilder der beiden Karten übereinstimmen."
      ),
      unitTestTestConfig(6, "Die Funktion playable_cards soll die Handkarten zurückgeben, die spielbar sind."),
      unitTestTestConfig(7, "Die Funktion playable_cards soll alle Handkarten zurückgeben, die spielbar sind."),
      unitTestTestConfig(
        8,
        "Die Funktion playable_cards soll eine leere Liste zurückgeben, falls keine Handkarten vorhanden sind."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl7Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Mau-Mau",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Classes, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
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
