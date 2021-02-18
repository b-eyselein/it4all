package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex2 extends ProgrammingInitialExercise(2, 2, "floating_point_exponential") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Mantisse soll alle Dezimalstellen beinhalten."),
      unitTestTestConfig(
        2,
        "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert und nicht multipliziert werden."
      ),
      unitTestTestConfig(3, "Die Zahl 10 soll mit der Variable <code>exponent</code> potenziert werden."),
      unitTestTestConfig(4, "Die Ausgabe soll richtig formatiert sein {Mantisse}e{Exponent}.")
    ),
    testFileName = "test_floating_point_exponential.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_floating_point_exponential.py")
  )

  private val implementationPart = ImplementationPart(
    base = """from math import log10
             |
             |
             |def format_floating_point_exponential(number: float) -> str:
             |    pass""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_floating_point_exponential.py", fileType),
        FileLoadConfig(
          "floating_point_exponential.py",
          fileType,
          editable = true,
          Some("floating_point_exponential_declaration.py")
        )
      )
    ),
    implFileName = "floating_point_exponential.py",
    sampleSolFileNames = Seq("floating_point_exponential.py")
  )

  val programmingColl2Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Fließkommazahl in Exponentialschreibweise",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
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
