package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex6 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 6)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "xmas_tree.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "xmas_tree_declaration.py")
      ),
      ExerciseFile(
        name = "test_xmas_tree.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_xmas_tree_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "xmas_tree_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = """Die Funktion xmas_tree_top_simple soll die Ränder mit einer \# kennzeichnen.""",
        file = ExerciseFile(
          name = "xmas_tree_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = """Die Funktion xmas_tree_top_simple soll den Zwischenraum (kein Rand und kein Weihnachtsbaum)
                        |durch die korrekte Anzahl an Leerzeichen füllen.""".stripMargin,
        file = ExerciseFile(
          name = "xmas_tree_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Die Funktion xmas_tree_top_simple soll den Weihnachtsbaum durch die korrekte Anzahl an * kennzeichnen.",
        file = ExerciseFile(
          name = "xmas_tree_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = """Die Funktion xmas_tree_top_design soll die Ränder mit einer \# kennzeichnen""",
        file = ExerciseFile(
          name = "xmas_tree_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = """Die Funktion xmas_tree_top_design soll den Zwischenraum (kein Rand und kein Weihnachtsbaum)
                        |durch die korrekte Anzahl an Leerzeichen füllen.""".stripMargin,
        file = ExerciseFile(
          name = "xmas_tree_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion xmas_tree_top_design soll den Weihnachtsbaum durch die korrekte Anzahl an * kennzeichnen.",
        file = ExerciseFile(
          name = "xmas_tree_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description =
          "Die Funktion xmas_tree_top_design soll die korrekte Anzahl Dekoration in den Weihnachtsbaum einfügen.",
        file = ExerciseFile(
          name = "xmas_tree_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = """Die Funktion xmas_tree_stub soll die Ränder mit einer \# kennzeichnen.""",
        file = ExerciseFile(
          name = "xmas_tree_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description =
          "Die Funktion xmas_tree_stub soll den Zwischenraum (kein Rand und kein Stumpf) durch die korrekte Anzahl an Leerzeichen füllen.",
        file = ExerciseFile(
          name = "xmas_tree_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Die Funktion xmas_tree_stub soll den Stumpf durch die korrekte Anzahl an I kennzeichnen.",
        file = ExerciseFile(
          name = "xmas_tree_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_10.py")
        )
      ),
      UnitTestTestConfig(
        id = 11,
        shouldFail = true,
        description = "Die Funktion xmas_tree_simple soll den korrekten Weihnachtsbaum ohne Dekoration erstellen.",
        file = ExerciseFile(
          name = "xmas_tree_11.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_11.py")
        )
      ),
      UnitTestTestConfig(
        id = 12,
        shouldFail = true,
        description = "Die Funktion xmas_tree_simple soll den korrekten Weihnachtsbaum ohne Dekoration erstellen.",
        file = ExerciseFile(
          name = "xmas_tree_12.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_12.py")
        )
      ),
      UnitTestTestConfig(
        id = 13,
        shouldFail = true,
        description = "Die Funktion xmas_tree_design soll den korrekten Weihnachtsbaum mit Dekoration erstellen.",
        file = ExerciseFile(
          name = "xmas_tree_13.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_13.py")
        )
      ),
      UnitTestTestConfig(
        id = 14,
        shouldFail = true,
        description = "Die Funktion xmas_tree_design soll den korrekten Weihnachtsbaum mit Dekoration erstellen.",
        file = ExerciseFile(
          name = "xmas_tree_14.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "xmas_tree_14.py")
        )
      )
    ),
    testFileName = "test_xmas_tree.py",
    folderName = "xmas_tree",
    sampleSolFileNames = Seq("test_xmas_tree.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_xmas_tree.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_xmas_tree.py")
      ),
      ExerciseFile(
        name = "xmas_tree.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "xmas_tree_declaration.py")
      )
    ),
    implFileName = "xmas_tree.py",
    sampleSolFileNames = Seq("xmas_tree.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "xmas_tree.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "xmas_tree.py")
          ),
          ExerciseFile(
            name = "test_xmas_tree.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_xmas_tree.py")
          )
        )
      )
    )
  )

  val programmingColl2Ex6: ProgrammingExercise = Exercise(
    exerciseId = 6,
    collectionId = 2,
    toolId,
    title = "Weihnachtsbaum",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = "xmas_tree",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
