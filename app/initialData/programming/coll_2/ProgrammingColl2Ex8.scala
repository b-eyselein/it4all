package initialData.programming.coll_2

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex8 extends ProgrammingInitialExercise(2, 8, "ceasar") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false)
      // FIXME: more unit tests...
    ),
    testFileName = "test_ceasar.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_ceasar.py")
  )

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
        content = loadTextFromFile(exResPath / "ceasar_declaration.py"),
        editable = true
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
