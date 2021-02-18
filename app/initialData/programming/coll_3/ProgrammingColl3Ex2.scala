package initialData.programming.coll_3

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl3Ex2 extends ProgrammingInitialExercise(3, 2, "discount") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Falls der Kunde einen Hund besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(2, "Falls der Kunde eine Katze besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(
        3,
        "Falls der Kunde einen Hund und eine Katze besitzt, soll der korrekte Rabatt gewährt werden."
      ),
      unitTestTestConfig(
        4,
        "Falls der Kunde keinen Hund und keine Katze besitzt, soll der korrekte Rabatt gewährt werden."
      ),
      unitTestTestConfig(5, "Falls der Kunde einen Hamster besitzt, soll der korrekte Rabatt gewährt werden."),
      unitTestTestConfig(6, "Falls der Kunde keinen Hamster besitzt, soll der korrekte Rabatt gewährt werden.")
    ),
    testFileName = "test_discount.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_discount.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def calculate_discount(has_dog: bool, has_cat: bool, has_hamster: bool) -> int:
             |pass
             |""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_discount.py", fileType),
        FileLoadConfig("discount.py", fileType, editable = true, Some("discount_declaration.py"))
      )
    ),
    implFileName = "discount.py",
    sampleSolFileNames = Seq("discount.py")
  )

  val programmingColl3Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Rabatt",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
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
