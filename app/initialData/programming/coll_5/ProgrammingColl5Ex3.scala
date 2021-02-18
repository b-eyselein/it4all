package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData.loadTextFromFile
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex3 extends ProgrammingInitialExercise(5, 3, "tuples_and_dicts") {

  private val unitTestPart = NormalUnitTestPart(
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
    testFileName = "test_tuples_and_dicts.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_tuples_and_dicts.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_tuples_and_dicts.py", fileType),
        FileLoadConfig("tuples_and_dicts.py", fileType, editable = true, Some("tuples_and_dicts_declaration.py"))
      )
    ),
    implFileName = "tuples_and_dicts.py",
    sampleSolFileNames = Seq("tuples_and_dicts.py")
  )

  val programmingColl5Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Tupel und Dictionaries",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Tuples, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Dicts, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
