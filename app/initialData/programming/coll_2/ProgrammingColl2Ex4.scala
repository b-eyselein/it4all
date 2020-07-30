package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex4 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 4)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "name_search.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "name_search_declaration.py")
      ),
      ExerciseFile(
        name = "test_name_search.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_name_search_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "name_search_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "name_search_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Es soll das korrekte Präfix zurückgegeben werden.",
        file = ExerciseFile(
          name = "name_search_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "name_search_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Es soll das korrekte Suffix zurückgegeben werden.",
        file = ExerciseFile(
          name = "name_search_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "name_search_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Falls das Fragment in mehreren Namen enthalten ist sollen alle Namen zurückgegeben werden.",
        file = ExerciseFile(
          name = "name_search_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "name_search_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Falls das Fragment in keinem Namen enthalten ist soll eine leere Liste zurückgegeben werden.",
        file = ExerciseFile(
          name = "name_search_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "name_search_4.py")
        )
      )
    ),
    testFileName = "test_name_search.py",
    folderName = "name_search",
    sampleSolFileNames = Seq("test_name_search.py")
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import Tuple, List


             |def name_search(all_names: List[str], fragment: str) -> List[Tuple[str, str]]:
             |    pass""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "test_name_search.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_name_search.py")
      ),
      ExerciseFile(
        name = "name_search.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "name_search_declaration.py")
      )
    ),
    implFileName = "name_search.py",
    sampleSolFileNames = Seq("name_search.py")
  )
  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "name_search.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "name_search.py")
          ),
          ExerciseFile(
            name = "test_name_search.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_name_search.py")
          )
        )
      )
    )
  )

  val programmingColl2Ex4: ProgrammingExercise = Exercise(
    exerciseId = 4,
    collectionId = 2,
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
      filename = "name_search",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
