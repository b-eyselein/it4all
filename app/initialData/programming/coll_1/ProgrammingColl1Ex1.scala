package initialData.programming.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex1 extends ProgrammingInitialExercise(1, 1, "ggt") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Diese Implementierung ist korrekt und sollte alle Tests bestehen.", shouldFail = false),
      unitTestTestConfig(1, "TODO!"),
      unitTestTestConfig(2, "TODO!"),
      unitTestTestConfig(3, "TODO!")
    ),
    testFileName = "test_ggt.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_ggt.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def ggt(a: int, b: int) -> int:
             |    return 0
             |""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_ggt.py", fileType),
        FileLoadConfig("ggt.py", fileType, editable = true, Some("ggt_declaration.py"))
      )
    ),
    implFileName = "ggt.py",
    sampleSolFileNames = Seq("ggt.py")
  )

  val programmingColl1Ex1: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Größter gemeinsamer Teiler",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 2,
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
