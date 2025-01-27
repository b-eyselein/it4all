package initialData.programming.coll_2

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl2Ex5 extends ProgrammingInitialExerciseContainer(2, 5, "three_chinese") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Vokale sollen durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(2, "Aufeinanderfolgende Vokale sollen auf einen einzigen Vokal reduziert werden."),
      unitTestTestConfig(3, "Der Vokal A bzw. a soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(4, "Der Vokal E bzw. e soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(5, "Der Vokal I bzw. i soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(6, "Der Vokal O bzw. o soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(7, "Der Vokal U bzw. u soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(8, "Der Umlaut Ä bzw. ä soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(9, "Der Umlaut Ö bzw. ö soll durch den übergebenen Vokal ersetzt werden."),
      unitTestTestConfig(10, "Der Umlaut Ü bzw. ü soll durch den übergebenen Vokal ersetzt werden.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex5: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Drei Chinesen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Strings  -> Level.Beginner,
      ProgrammingTopics.ForLoops -> Level.Beginner,
      ProgrammingTopics.Lists    -> Level.Beginner
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
