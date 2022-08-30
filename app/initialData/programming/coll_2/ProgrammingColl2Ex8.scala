package initialData.programming.coll_2

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl2Ex8 extends ProgrammingInitialExerciseContainer(2, 8, "ceasar") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false)
      // FIXME: more unit tests...
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex8: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Caesar-Verschlüsselung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Maths      -> Level.Beginner,
      ProgrammingTopics.Strings    -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner
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
