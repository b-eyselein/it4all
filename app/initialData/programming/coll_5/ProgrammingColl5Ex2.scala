package initialData.programming.coll_5

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl5Ex2 extends ProgrammingInitialExercise(5, 2) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("dicts.py", fileType, maybeOtherFileName = Some("dicts_declaration.py")),
        FileLoadConfig("test_dicts.py", fileType, editable = true, Some("test_dicts_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "dicts_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Funktion count_char_occurences soll nicht zwischen Groß- und Kleinschreibung unterscheiden.",
        file = ExerciseFile(
          name = "dicts_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Die Funktion count_char_occurences soll die einzelnen Buchstaben korrekt zählen.",
        file = ExerciseFile(
          name = "dicts_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Falls der übergebene String leer ist, soll die Funktion count_char_occurences ein leeres Dictionary zurückgeben.",
        file = ExerciseFile(
          name = "dicts_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion word_position_list soll die korrekten Indizes der Wörter angeben.",
        file = ExerciseFile(
          name = "dicts_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion word_position_list soll alle Indizes angeben, falls ein Wort mehrmals vorkommt.",
        file = ExerciseFile(
          name = "dicts_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          """Falls der übergebene String leer ist, soll die Funktion word_position_list ein leeres Dictionary zurückgeben. !!!TODO - Falls der Test self.assertEqual({}, word_position_list("")) eingefügt wird, werden auch alle anderen Tests der anderen Methoden grün!!!""",
        file = ExerciseFile(
          name = "dicts_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Die Funktion merge_dicts_with_add soll die beiden Dictionaries zusammenfügen.",
        file = ExerciseFile(
          name = "dicts_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description =
          "Die Funktion merge_dicts_with_add soll die Werte im Ergebnisdictionary, bei gleichen Schlüsseln in den Ausgangsdictionaries, addieren.",
        file = ExerciseFile(
          name = "dicts_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description =
          "Falls beide Ausgangsdictionaries leer sind, soll die Funktion merge_dicts_with_add ein leeres Dictionary zurückgeben.",
        file = ExerciseFile(
          name = "dicts_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "dicts_9.py")
        )
      )
    ),
    testFileName = "test_dicts.py",
    folderName = "dicts",
    sampleSolFileNames = Seq("test_dicts.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_dicts.py", fileType),
        FileLoadConfig("dicts.py", fileType, editable = true, Some("dicts_declaration.py"))
      )
    ),
    implFileName = "dicts.py",
    sampleSolFileNames = Seq("dicts.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("dicts.py", fileType),
      FileLoadConfig("test_dicts.py", fileType)
    )
  )

  val programmingColl5Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Dictionaries",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "dicts",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
