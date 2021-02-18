package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex4 extends ProgrammingInitialExercise(5, 4, "reindeers") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests descriptions!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Funktion calculate_bmi soll den korrekten BMI berechnen."),
      unitTestTestConfig(2, "Die Funktion calculate_bmi soll den korrekten BMI berechnen."),
      unitTestTestConfig(3, "Die Funktion calculate_bmi soll den korrekten BMI berechnen."),
      unitTestTestConfig(
        4,
        "Die Funktion calculate_reindeer_bmis soll den korrekten BMI für jedes Rentier zurückgeben."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion calculate_reindeer_bmis soll den korrekten Namen für jedes Rentier zurückgeben."
      ),
      unitTestTestConfig(
        6,
        "Die Funktion calculate_reindeer_bmis soll ein leeres Dictionary zurückgeben, falls das übergebene Dictionary leer ist."
      )
    ),
    testFileName = "test_reindeers.py",
    folderName = exerciseBaseName,
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )
}
