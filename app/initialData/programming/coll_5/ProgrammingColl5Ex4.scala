package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex4 extends ProgrammingInitialExercise(5, 4) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("reindeers.py", fileType, maybeOtherFileName = Some("reindeers_declaration.py")),
        FileLoadConfig("test_reindeers.py", fileType, editable = true, Some("test_reindeers_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "reindeers_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Funktion calculate_bmi soll den korrekten BMI berechnen.",
        file = ExerciseFile(
          name = "reindeers_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Die Funktion calculate_bmi soll den korrekten BMI berechnen.",
        file = ExerciseFile(
          name = "reindeers_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion calculate_bmi soll den korrekten BMI berechnen.",
        file = ExerciseFile(
          name = "reindeers_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion calculate_reindeer_bmis soll den korrekten BMI für jedes Rentier zurückgeben.",
        file = ExerciseFile(
          name = "reindeers_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion calculate_reindeer_bmis soll den korrekten Namen für jedes Rentier zurückgeben.",
        file = ExerciseFile(
          name = "reindeers_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion calculate_reindeer_bmis soll ein leeres Dictionary zurückgeben, falls das übergebene Dictionary leer ist.",
        file = ExerciseFile(
          name = "reindeers_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "reindeers_6.py")
        )
      )
    ),
    testFileName = "test_reindeers.py",
    folderName = "reindeers",
    sampleSolFileNames = Seq("test_reindeers.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_reindeers.py", fileType),
        FileLoadConfig("reindeers.py", fileType, editable = true, Some("reindeers_declaration.py"))
      )
    ),
    implFileName = "reindeers.py",
    sampleSolFileNames = Seq("reindeers.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("reindeers.py", fileType),
      FileLoadConfig("test_reindeers.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl5Ex4: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Fitnessprogramm",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Dicts, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = "reindeers",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )
}
