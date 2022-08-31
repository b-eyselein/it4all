package initialData.programming.coll_4

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl4Ex2 extends ProgrammingInitialExerciseContainer(4, 2, "longest_string") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "TODO!"),
      unitTestTestConfig(2, "TODO!")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl4Ex2: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Längste Zeichenkette",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.ForLoops   -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner,
      ProgrammingTopics.Lists      -> Level.Beginner,
      ProgrammingTopics.Strings    -> Level.Beginner
    ),
    difficulty = Level.Intermediate,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
