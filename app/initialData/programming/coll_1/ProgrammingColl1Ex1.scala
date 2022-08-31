package initialData.programming.coll_1

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl1Ex1 extends ProgrammingInitialExerciseContainer(1, 1, "ggt") {

  val programmingColl1Ex1: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Größter gemeinsamer Teiler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = Level.Intermediate,
    topicsWithLevels = Map(
      ProgrammingTopics.ForLoops   -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner,
      ProgrammingTopics.Maths      -> Level.Beginner
    ),
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart = UnitTestPart(
        // FIXME: unit tests description!
        unitTestsDescription = "TODO!",
        unitTestFiles = unitTestFiles,
        unitTestTestConfigs = Seq(
          unitTestTestConfig(0, "Diese Implementierung ist korrekt und sollte alle Tests bestehen.", shouldFail = false),
          unitTestTestConfig(1, "TODO!"),
          unitTestTestConfig(2, "TODO!"),
          unitTestTestConfig(3, "TODO!")
        ),
        testFileName = testFileName,
        folderName = exerciseBaseName
      ),
      defaultImplementationPart,
      sampleSolutions = Seq(
        FilesSolution(
          defaultSampleSolutionFiles
        )
      )
    )
  )

}
