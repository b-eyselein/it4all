package initialData.programming.coll_2

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex7 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 2, 7)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "a1z26.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "a1z26_declaration.py")
      ),
      ExerciseFile(
        name = "test_a1z26.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_a1z26_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "a1z26_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "encrypt_letter soll den übergebenen Buchstaben durch die korrekte Ganzzahl verschlüsseln.",
        file = ExerciseFile(
          name = "a1z26_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "decrypt_letter soll die übergebene Ganzzahl in den korrekten Buchstaben entschlüsseln.",
        file = ExerciseFile(
          name = "a1z26_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "encrypt_word soll jeden Buchstaben des übergebenen Wortes verschlüsseln und alle Ganzzahlen in der Liste zurückgegeben.",
        file = ExerciseFile(
          name = "a1z26_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "encrpyt_word soll die richtigen, im übergebenen Wort enthaltenen, Buchstaben verschlüsseln",
        file = ExerciseFile(
          name = "a1z26_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "encrypt_word soll bei einem leeren String eine leere Liste zurückgeben.",
        file = ExerciseFile(
          name = "a1z26_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "decrypt_word soll das gesamte entschlüsselte Wort zurückgeben.",
        file = ExerciseFile(
          name = "a1z26_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "decrypt_word soll die korrekte Ganzzahl entschlüsseln.",
        file = ExerciseFile(
          name = "a1z26_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "decrypt_word soll bei einer leeren Liste einen leeren String zurückgeben.",
        file = ExerciseFile(
          name = "a1z26_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "a1z26_8.py")
        )
      )
    ),
    testFileName = "test_a1z26.py",
    sampleSolFileNames = Seq("test_a1z26.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_a1z26.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_a1z26.py")
      ),
      ExerciseFile(
        name = "a1z26.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "a1z26_declaration.py")
      )
    ),
    implFileName = "a1z26.py",
    sampleSolFileNames = Seq("a1z26.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "a1z26.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "a1z26.py")
          ),
          ExerciseFile(
            name = "test_a1z26.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_a1z26.py")
          )
        )
      )
    )
  )

  val programmingColl2Ex7: ProgrammingExercise = Exercise(
    exerciseId = 7,
    collectionId = 2,
    toolId,
    title = "A1Z26-Verschlüsselung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      functionName = "a1z26",
      foldername = "a1z26",
      filename = "a1z26",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}
