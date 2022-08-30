package initialData.programming.coll_2

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl2Ex7 extends ProgrammingInitialExerciseContainer(2, 7, "a1z26") {

  private val unitTestPart = UnitTestPart(
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
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex7: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "A1Z26-Verschlüsselung",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Strings  -> Level.Beginner,
      ProgrammingTopics.ForLoops -> Level.Beginner,
      ProgrammingTopics.Maths    -> Level.Beginner
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
