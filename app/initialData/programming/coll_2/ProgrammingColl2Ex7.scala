package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex7 extends ProgrammingInitialExercise(2, 7) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("a1z26.py", fileType, maybeOtherFileName = Some("a1z26_declaration.py")),
        FileLoadConfig("test_a1z26.py", fileType, editable = true, Some("test_a1z26_declaration.py"))
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
    folderName = "a1z26",
    sampleSolFileNames = Seq("test_a1z26.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_a1z26.py", fileType),
        FileLoadConfig("a1z26.py", fileType, editable = true, Some("a1z26_declaration.py"))
      )
    ),
    implFileName = "a1z26.py",
    sampleSolFileNames = Seq("a1z26.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("a1z26.py", fileType),
      FileLoadConfig("test_a1z26.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

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
      filename = "a1z26",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
