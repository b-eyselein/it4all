package initialData.programming.coll_7

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl7Ex2 extends ProgrammingInitialExerciseContainer(7, 2, "maumau") {

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

  val programmingColl7Ex2: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Mau-Mau",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Classes    -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner,
      ProgrammingTopics.Lists      -> Level.Beginner,
      ProgrammingTopics.Strings    -> Level.Beginner
    ),
    difficulty = Level.Beginner,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
