package initialData.programming.coll_5

import initialData.InitialData.loadTextFromFile
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl5Ex3 extends ProgrammingInitialExerciseContainer(5, 3, "tuples_and_dicts") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(id = 0, description = "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion tuple_list_to_dict soll den ersten Eintrag im Tupel als Schlüssel und den zweiten Eintrag als Wert setzen."
      ),
      unitTestTestConfig(
        2,
        "Die Funktion tuple_list_to_dict soll den ersten Wert verwenden, falls ein Schlüssel mehrmals vorkommt."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion tuple_list_to_dict soll ein leeres Dictionary zurückgeben, falls eine leere Liste übergeben wird."
      ),
      unitTestTestConfig(
        4,
        "Die Funktion intersect_dicts soll ein leeres Dictionary zurückgeben, falls keine übereinstimmenden Schlüsseln in den übergebenen Dictionaries existieren."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion intersect_dicts soll ein Dictionary mit den korrekten Werten bei übereinstimmenden Schlüsseln zurückgeben."
      ),
      unitTestTestConfig(
        6,
        "Die Funktion intersect_dicts soll ein leeres Dictionary zurückgeben, falls die beiden übergebenen Dictionaries leer sind."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl5Ex3: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Tupel und Dictionaries",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Lists      -> Level.Beginner,
      ProgrammingTopics.Tuples     -> Level.Beginner,
      ProgrammingTopics.Dicts      -> Level.Beginner,
      ProgrammingTopics.ForLoops   -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner
    ),
    difficulty = Level.Advanced,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
