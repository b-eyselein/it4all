package initialData.programming.coll_4

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl4Ex4 extends ProgrammingInitialExerciseContainer(4, 4, "slicing") {

  private val unitTestPart = UnitTestPart(
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
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl4Ex4: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Slicing",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Lists   -> Level.Beginner,
      ProgrammingTopics.Strings -> Level.Beginner
    ),
    difficulty = Level.Intermediate,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
