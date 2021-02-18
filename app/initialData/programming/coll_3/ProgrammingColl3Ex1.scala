package initialData.programming.coll_3

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl3Ex1 extends ProgrammingInitialExercise(3, 1, "lottery") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Bei Gewinnstufe 0 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(2, "Bei Gewinnstufe 1 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(3, "Bei Gewinnstufe 2 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(4, "Bei Gewinnstufe 3 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(5, "Bei Gewinnstufe 4 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(6, "Bei Gewinnstufe 5 soll der korrekte Gewinn zurückgegeben werden."),
      unitTestTestConfig(7, "Bei einer Gewinnststufe kleiner 0 oder größer 5 soll die Gewinnstufe 0 angenommen werden.")
    ),
    testFileName = "test_lottery.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_lottery.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def calculate_lottery_win(pot: float, win_class: int) > float:
             |    pass
             |""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_lottery.py", fileType),
        FileLoadConfig("lottery.py", fileType, editable = true, Some("lottery_declaration.py"))
      )
    ),
    implFileName = "lottery.py",
    sampleSolFileNames = Seq("lottery.py")
  )

  val programmingColl3Ex1: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Lotterie",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
