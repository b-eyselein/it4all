package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex5 extends ProgrammingInitialExercise(2, 5) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("three_chinese.py", fileType, maybeOtherFileName = Some("three_chinese_declaration.py")),
        FileLoadConfig("test_three_chinese.py", fileType, editable = true, Some("test_three_chinese_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "three_chinese_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Vokale sollen durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Aufeinanderfolgende Vokale sollen auf einen einzigen Vokal reduziert werden.",
        file = ExerciseFile(
          name = "three_chinese_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Der Vokal A bzw. a soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Der Vokal E bzw. e soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Der Vokal I bzw. i soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Der Vokal O bzw. o soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Der Vokal U bzw. u soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Der Umlaut Ä bzw. ä soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Der Umlaut Ö bzw. ö soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Der Umlaut Ü bzw. ü soll durch den übergebenen Vokal ersetzt werden.",
        file = ExerciseFile(
          name = "three_chinese_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "three_chinese_10.py")
        )
      )
    ),
    testFileName = "test_three_chinese.py",
    folderName = "three_chinese",
    sampleSolFileNames = Seq("test_three_chinese.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def three_chinese(line: str, target_vowel: str) -> str:
             |    pass""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_three_chinese.py", fileType),
        FileLoadConfig("three_chinese.py", fileType, editable = true, Some("three_chinese_declaration.py"))
      )
    ),
    implFileName = "three_chinese.py",
    sampleSolFileNames = Seq("three_chinese.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("three_chinese.py", fileType),
      FileLoadConfig("test_three_chinese.py", fileType)
    )
  )

  val programmingColl2Ex5: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Drei Chinesen",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "three_chinese",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
