package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex8 extends ProgrammingInitialExercise(2, 8) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("ceasar.py", fileType, maybeOtherFileName = Some("ceasar_declaration.py")),
        FileLoadConfig("test_ceasar.py", fileType, editable = true, Some("test_ceasar_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "caesar_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "ceasar_0.py")
        )
      )
    ),
    testFileName = "test_ceasar.py",
    folderName = "ceasar",
    sampleSolFileNames = Seq("test_ceasar.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("ceasar.py", fileType),
      FileLoadConfig("test_ceasar.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_ceasar.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_ceasar.py")
      ),
      ExerciseFile(
        name = "ceasar.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "ceasar_declaration.py")
      )
    ),
    implFileName = "ceasar.py",
    sampleSolFileNames = Seq("ceasar.py")
  )

  val programmingColl2Ex8: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Caesar-Verschlüsselung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "ceasar",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
