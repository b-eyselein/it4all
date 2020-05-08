package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex8 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 8)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "ceasar.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "ceasar_declaration.py")
      ),
      ExerciseFile(
        name = "test_ceasar.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_ceasar_declaration.py")
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
    sampleSolFileNames = Seq("test_ceasar.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "ceasar.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "ceasar.py")
          ),
          ExerciseFile(
            name = "test_ceasar.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_ceasar.py")
          )
        )
      )
    )
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
        editable = true,
        content = loadTextFromFile(exResPath / "ceasar_declaration.py")
      )
    ),
    implFileName = "ceasar.py",
    sampleSolFileNames = Seq("ceasar.py")
  )

  val programmingColl2Ex8: ProgrammingExercise = Exercise(
    exerciseId = 8,
    collectionId = 2,
    toolId,
    title = "Caesar-Verschlüsselung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Classes, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      functionName = "ceasar",
      foldername = "ceasar",
      filename = "ceasar",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}
