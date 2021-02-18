package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex4 extends ProgrammingInitialExercise(2, 4, "name_search") {

  private val unitTestPart = NormalUnitTestPart(
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
    testFileName = "test_name_search.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_name_search.py")
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import Tuple, List


             |def name_search(all_names: List[str], fragment: str) -> List[Tuple[str, str]]:
             |    pass""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_name_search.py", fileType),
        FileLoadConfig("name_search.py", fileType, editable = true, Some("name_search_declaration.py"))
      )
    ),
    implFileName = "name_search.py",
    sampleSolFileNames = Seq("name_search.py")
  )

  val programmingColl2Ex4: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Namenssuche",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Tuples, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
