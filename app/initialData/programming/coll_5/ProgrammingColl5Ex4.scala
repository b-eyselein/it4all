package initialData.programming.coll_5

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex4 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 5, 4)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "reindeers.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "reindeers_declaration.py")
      ),
      ExerciseFile(
        name = "test_reindeers.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_reindeers_declaration.py")
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
    files = Seq(
      ExerciseFile(
        name = "test_reindeers.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_reindeers.py")
      ),
      ExerciseFile(
        name = "reindeers.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "reindeers_declaration.py")
      )
    ),
    implFileName = "reindeers.py",
    sampleSolFileNames = Seq("reindeers.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "reindeers.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "reindeers.py")
          ),
          ExerciseFile(
            name = "test_reindeers.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_reindeers.py")
          )
        )
      )
    )
  )

  val programmingColl5Ex4: ProgrammingExercise = Exercise(
    exerciseId = 4,
    collectionId = 5,
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
