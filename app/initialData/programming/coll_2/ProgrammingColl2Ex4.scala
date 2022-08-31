package initialData.programming.coll_2

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl2Ex4 extends ProgrammingInitialExerciseContainer(2, 4, "name_search") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Es soll das korrekte Präfix zurückgegeben werden."),
      unitTestTestConfig(2, "Es soll das korrekte Suffix zurückgegeben werden."),
      unitTestTestConfig(
        3,
        "Falls das Fragment in mehreren Namen enthalten ist sollen alle Namen zurückgegeben werden."
      ),
      unitTestTestConfig(
        4,
        "Falls das Fragment in keinem Namen enthalten ist soll eine leere Liste zurückgegeben werden."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex4: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Namenssuche",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Strings  -> Level.Beginner,
      ProgrammingTopics.Lists    -> Level.Beginner,
      ProgrammingTopics.Tuples   -> Level.Beginner,
      ProgrammingTopics.ForLoops -> Level.Beginner
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
