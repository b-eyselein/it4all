package initialData.programming.coll_2

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex7 extends ProgrammingInitialExercise(2, 7, "a1z26") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "encrypt_letter soll den übergebenen Buchstaben durch die korrekte Ganzzahl verschlüsseln."
      ),
      unitTestTestConfig(2, "decrypt_letter soll die übergebene Ganzzahl in den korrekten Buchstaben entschlüsseln."),
      unitTestTestConfig(
        3,
        "encrypt_word soll jeden Buchstaben des übergebenen Wortes verschlüsseln und alle Ganzzahlen in der Liste zurückgegeben."
      ),
      unitTestTestConfig(
        4,
        "encrpyt_word soll die richtigen, im übergebenen Wort enthaltenen, Buchstaben verschlüsseln"
      ),
      unitTestTestConfig(5, "encrypt_word soll bei einem leeren String eine leere Liste zurückgeben."),
      unitTestTestConfig(6, "decrypt_word soll das gesamte entschlüsselte Wort zurückgeben."),
      unitTestTestConfig(7, "decrypt_word soll die korrekte Ganzzahl entschlüsseln."),
      unitTestTestConfig(8, "decrypt_word soll bei einer leeren Liste einen leeren String zurückgeben.")
    ),
    testFileName = "test_a1z26.py",
    folderName = exerciseBaseName,
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
