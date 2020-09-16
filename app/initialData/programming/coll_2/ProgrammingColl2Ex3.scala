package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex3 extends ProgrammingInitialExercise(2, 3) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig(
          "file_name_and_ending.py",
          fileType,
          maybeOtherFileName = Some("file_name_and_ending_declaration.py")
        ),
        FileLoadConfig(
          "test_file_name_and_ending.py",
          fileType,
          editable = true,
          Some("test_file_name_and_ending_declaration.py")
        )
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "file_name_and_ending_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Bei Dateien ohne Endung soll nur der Dateiname und ein leerer String zurückgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Bei versteckten Dateien soll der Dateiname mit einem vorangestellten Punkt und ein leerer String zurückgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Bei versteckten Dateien soll nur der Dateiname mit einem vorangstelltem Punkt und ein leerer String zurückgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Bei normalen Dateien soll der Dateiname und die Dateiendung zurückgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Bei normalen Dateien soll der Dateiname und die Endung in der korrekten Reihenfolge zurückgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Bei Dateien mit mehreren Endungen soll der Dateiname mit allen Endungen außer der letzten und die letzte Endung ausgegeben werden.",
        file = ExerciseFile(
          name = "file_name_and_ending_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "file_name_and_ending_6.py")
        )
      )
    ),
    testFileName = "test_file_name_and_ending.py",
    folderName = "file_name_and_ending",
    sampleSolFileNames = Seq("test_file_name_and_ending.py")
  )

  private val implementationPart = ImplementationPart(
    base = """from typing import Tuple
             |
             |
             |def file_name_and_ending(filename: str) -> Tuple[str, str]:
             |    pass""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_file_name_and_ending.py", fileType),
        FileLoadConfig(
          "file_name_and_ending.py",
          fileType,
          editable = true,
          Some("file_name_and_ending_declaration.py")
        )
      )
    ),
    implFileName = "file_name_and_ending.py",
    sampleSolFileNames = Seq("file_name_and_ending.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("file_name_and_ending.py", fileType),
      FileLoadConfig("test_file_name_and_ending.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl2Ex3: ProgrammingExercise = Exercise(
    exerciseId = 3,
    collectionId = 2,
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
      filename = "file_name_and_ending",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
