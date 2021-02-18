package initialData.programming.coll_4

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl4Ex4 extends ProgrammingInitialExercise(4, 4, "slicing") {

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(id = 0, description = "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die Funktion even_indexes soll alle Elemente an geraden Indizes zurückgeben."),
      unitTestTestConfig(
        2,
        "Die Funktion even_indexes soll alle Elemente an geraden Indizes und nicht an ungeraden Indizes zurückgeben."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion even_indexes soll alle Elemente an gerade Indizes und nicht am 0. Index zurückgeben."
      ),
      unitTestTestConfig(4, "Die Funktion reversed_special soll vom vorletzten Element aus beginnen."),
      unitTestTestConfig(
        5,
        "Die Funktion reversed_special soll jedes dritte Element, vom vorletzten Element aus beginnend, ausgeben."
      ),
      unitTestTestConfig(6, "Die Funktion first_half soll die erste Hälfte der Liste zurückgeben."),
      unitTestTestConfig(7, "Die Funktion first_half soll bei ungeraden Längen abrunden."),
      unitTestTestConfig(
        8,
        "Die Funktion rotate_right soll für jede Umdrehung das letzte Element der Liste an den Anfang der Liste setzen."
      ),
      unitTestTestConfig(
        9,
        "Die Funktion rotate_right soll die korrekte Anzahl an übergebenen Umdrehungen durchführen."
      )
    ),
    testFileName = "test_slicing.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_slicing.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_slicing.py", fileType),
        FileLoadConfig("slicing.py", fileType, editable = true, Some("slicing_declaration.py"))
      )
    ),
    implFileName = "slicing.py",
    sampleSolFileNames = Seq("slicing.py")
  )

  val programmingColl4Ex4: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Slicing",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
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
