package initialData.programming.coll_2

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex3 extends ProgrammingInitialExercise(2, 3, "file_name_and_ending") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Bei Dateien ohne Endung soll nur der Dateiname und ein leerer String zurückgegeben werden."
      ),
      unitTestTestConfig(
        2,
        "Bei versteckten Dateien soll der Dateiname mit einem vorangestellten Punkt und ein leerer String zurückgegeben werden."
      ),
      unitTestTestConfig(
        3,
        "Bei versteckten Dateien soll nur der Dateiname mit einem vorangstelltem Punkt und ein leerer String zurückgegeben werden."
      ),
      unitTestTestConfig(4, "Bei normalen Dateien soll der Dateiname und die Dateiendung zurückgegeben werden."),
      unitTestTestConfig(
        5,
        "Bei normalen Dateien soll der Dateiname und die Endung in der korrekten Reihenfolge zurückgegeben werden."
      ),
      unitTestTestConfig(
        6,
        "Bei Dateien mit mehreren Endungen soll der Dateiname mit allen Endungen außer der letzten und die letzte Endung ausgegeben werden."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Dateinamen und Endung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Tuples, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
