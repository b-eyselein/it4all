package initialData.programming.coll_4

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl4Ex3 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 4, 3)

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_general.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_general.py")
      ),
      ExerciseFile(
        name = "general.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "general_declaration.py")
      )
    ),
    implFileName = "general.py",
    sampleSolFileNames = Seq("general.py")
  )

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "general.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "general_declaration.py")
      ),
      ExerciseFile(
        name = "test_general.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_general_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "general_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Die Funktion filter_greater soll nur die Zahlen in einer Liste zurückgeben, die größer als die übergebene Zahl sind.",
        file = ExerciseFile(
          name = "general_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion filter_greater soll eine leere Liste zurückgeben, falls keine Zahl größer als die übergebene Zahl ist.",
        file = ExerciseFile(
          name = "general_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion count_lower soll die Zahlen zählen, welche kleiner als die übergebene Zahl sind.",
        file = ExerciseFile(
          name = "general_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description =
          "Die Funktion count_lower soll 0 zurückgeben, falls keine Zahl kleiner als die übergebene Zahl ist.",
        file = ExerciseFile(
          name = "general_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion bank_card_security_value soll die Zahlen mit den Inidzes multiplizieren.",
        file = ExerciseFile(
          name = "general_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion bank_card_security_value soll die Zahlen mit den korrekten Indizes mutltiplizieren.",
        file = ExerciseFile(
          name = "general_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Die Funktion bank_card_security_value soll 0 zurückgeben, falls die übergebene Liste leer ist.",
        file = ExerciseFile(
          name = "general_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Die Funktion vector_length soll alle Einträge zuerst quadrieren.",
        file = ExerciseFile(
          name = "general_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Die Funktion vector_length soll alle quadrierten Einträge addieren.",
        file = ExerciseFile(
          name = "general_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_9.py")
        )
      ),
      UnitTestTestConfig(
        id = 10,
        shouldFail = true,
        description = "Die Funktion vector_length soll die Wurzel der quadrierten und addierten Einträge zurückgeben.",
        file = ExerciseFile(
          name = "general_10.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_10.py")
        )
      ),
      UnitTestTestConfig(
        id = 11,
        shouldFail = true,
        description = "Die Funktion vector_length soll 0 zurückgeben, falls die übergebene Liste leer ist.",
        file = ExerciseFile(
          name = "general_11.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_11.py")
        )
      ),
      UnitTestTestConfig(
        id = 12,
        shouldFail = true,
        description =
          "Die Funktion vector_scalar soll den übergebenen Skalarwert zu jedem Element im Originalvektor addieren.",
        file = ExerciseFile(
          name = "general_12.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_12.py")
        )
      ),
      UnitTestTestConfig(
        id = 13,
        shouldFail = true,
        description =
          "Die Funktion vector_scalar soll den korrekten übergebenen Skalarwert zu jedem Element im Originalvektor addieren.",
        file = ExerciseFile(
          name = "general_13.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_13.py")
        )
      ),
      UnitTestTestConfig(
        id = 14,
        shouldFail = true,
        description =
          "Die Funktion vector_scalar soll eine leere Liste zurückgeben, falls die übergebene Liste leer ist.",
        file = ExerciseFile(
          name = "general_14.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_14.py")
        )
      ),
      UnitTestTestConfig(
        id = 15,
        shouldFail = true,
        description =
          "Die Funktion vector_add_vector soll eine leere Liste zurückgeben, falls die Längen der beiden übergebenen Listen nicht übereinstimmen.",
        file = ExerciseFile(
          name = "general_15.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_15.py")
        )
      ),
      UnitTestTestConfig(
        id = 16,
        shouldFail = true,
        description = "Die Funktion vector_add_vector soll die beiden Vektoren korrekt addieren.",
        file = ExerciseFile(
          name = "general_16.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_16.py")
        )
      ),
      UnitTestTestConfig(
        id = 17,
        shouldFail = true,
        description =
          "Die Funktion vector_add_vector soll eine leere Liste zurückgeben, falls beide übergebenen Listen leer sind.",
        file = ExerciseFile(
          name = "general_17.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_17.py")
        )
      ),
      UnitTestTestConfig(
        id = 18,
        shouldFail = true,
        description = "Die Funktion flatten_lists soll alle Listen korrekt in einer neuen Liste vereinen.",
        file = ExerciseFile(
          name = "general_18.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_18.py")
        )
      ),
      UnitTestTestConfig(
        id = 19,
        shouldFail = true,
        description =
          "Die Funktion flatten_lists soll eine leere Liste zurückgeben, falls die übergebene Liste leer ist.",
        file = ExerciseFile(
          name = "general_19.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "general_19.py")
        )
      )
    ),
    testFileName = "test_general.py",
    folderName = "general",
    sampleSolFileNames = Seq("test_general.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "general.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "general.py")
          ),
          ExerciseFile(
            name = "test_general.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_general.py")
          )
        )
      )
    )
  )

  val programmingColl4Ex3: ProgrammingExercise = Exercise(
    exerciseId = 3,
    collectionId = 4,
    toolId,
    title = "Allgemeine Aufgaben",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = "general",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
