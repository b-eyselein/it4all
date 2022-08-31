package initialData.programming.coll_5

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl5Ex2 extends ProgrammingInitialExerciseContainer(5, 2, "dicts") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(id = 0, description = "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion count_char_occurences soll nicht zwischen Groß- und Kleinschreibung unterscheiden."
      ),
      unitTestTestConfig(2, "Die Funktion count_char_occurences soll die einzelnen Buchstaben korrekt zählen."),
      unitTestTestConfig(
        3,
        "Falls der übergebene String leer ist, soll die Funktion count_char_occurences ein leeres Dictionary zurückgeben."
      ),
      unitTestTestConfig(4, "Die Funktion word_position_list soll die korrekten Indizes der Wörter angeben."),
      unitTestTestConfig(
        5,
        "Die Funktion word_position_list soll alle Indizes angeben, falls ein Wort mehrmals vorkommt."
      ),
      unitTestTestConfig(
        6,
        """Falls der übergebene String leer ist, soll die Funktion word_position_list ein leeres Dictionary zurückgeben. !!!TODO - Falls der Test self.assertEqual({}, word_position_list("")) eingefügt wird, werden auch alle anderen Tests der anderen Methoden grün!!!"""
      ),
      unitTestTestConfig(7, "Die Funktion merge_dicts_with_add soll die beiden Dictionaries zusammenfügen."),
      unitTestTestConfig(
        8,
        "Die Funktion merge_dicts_with_add soll die Werte im Ergebnisdictionary, bei gleichen Schlüsseln in den Ausgangsdictionaries, addieren."
      ),
      unitTestTestConfig(
        9,
        "Falls beide Ausgangsdictionaries leer sind, soll die Funktion merge_dicts_with_add ein leeres Dictionary zurückgeben."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl5Ex2: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Dictionaries",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Strings    -> Level.Beginner,
      ProgrammingTopics.ForLoops   -> Level.Beginner,
      ProgrammingTopics.Conditions -> Level.Beginner,
      ProgrammingTopics.Maths      -> Level.Beginner,
      ProgrammingTopics.Lists      -> Level.Beginner
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
