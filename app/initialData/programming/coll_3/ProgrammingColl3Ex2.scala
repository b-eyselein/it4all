package initialData.programming.coll_3

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl3Ex2 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 3, 2)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "discount.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "discount_declaration.py")
      ),
      ExerciseFile(
        name = "test_discount.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_discount_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "discount_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Falls der Kunde einen Hund besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Falls der Kunde eine Katze besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Falls der Kunde einen Hund und eine Katze besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Falls der Kunde keinen Hund und keine Katze besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Falls der Kunde einen Hamster besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Falls der Kunde keinen Hamster besitzt, soll der korrekte Rabatt gewährt werden.",
        file = ExerciseFile(
          name = "discount_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "discount_6.py")
        )
      )
    ),
    testFileName = "test_discount.py",
    folderName = "discount",
    sampleSolFileNames = Seq("test_discount.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def calculate_discount(has_dog: bool, has_cat: bool, has_hamster: bool) -> int:
             |pass
             |""".stripMargin,
    files = Seq(
      ExerciseFile(
        name = "test_discount.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_discount.py")
      ),
      ExerciseFile(
        name = "discount.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "discount_declaration.py")
      )
    ),
    implFileName = "discount.py",
    sampleSolFileNames = Seq("discount.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "discount.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "discount.py")
          ),
          ExerciseFile(
            name = "test_discount.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_discount.py")
          )
        )
      )
    )
  )

  val programmingColl3Ex2: ProgrammingExercise = Exercise(
    exerciseId = 2,
    collectionId = 3,
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
      filename = "discount",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
