package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex5 extends ProgrammingInitialExercise(2, 5, "three_chinese") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("three_chinese.py", fileType, maybeOtherFileName = Some("three_chinese_declaration.py")),
        FileLoadConfig("test_three_chinese.py", fileType, editable = true, Some("test_three_chinese_declaration.py"))
      )
    ),
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
    testFileName = "test_three_chinese.py",
    folderName = exerciseBaseName,
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
