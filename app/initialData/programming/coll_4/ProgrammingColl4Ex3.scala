package initialData.programming.coll_4

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl4Ex3 extends ProgrammingInitialExercise(4, 3, "general") {

  // TODO: split in multiple, smaller exercises!

  private val unitTestPart = NormalUnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, description = "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Die Funktion filter_greater soll nur die Zahlen in einer Liste zurückgeben, die größer als die übergebene Zahl sind."
      ),
      unitTestTestConfig(
        2,
        "Die Funktion filter_greater soll eine leere Liste zurückgeben, falls keine Zahl größer als die übergebene Zahl ist."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion count_lower soll die Zahlen zählen, welche kleiner als die übergebene Zahl sind."
      ),
      unitTestTestConfig(
        4,
        "Die Funktion count_lower soll 0 zurückgeben, falls keine Zahl kleiner als die übergebene Zahl ist."
      ),
      unitTestTestConfig(5, "Die Funktion bank_card_security_value soll die Zahlen mit den Inidzes multiplizieren."),
      unitTestTestConfig(
        6,
        "Die Funktion bank_card_security_value soll die Zahlen mit den korrekten Indizes mutltiplizieren."
      ),
      unitTestTestConfig(
        7,
        "Die Funktion bank_card_security_value soll 0 zurückgeben, falls die übergebene Liste leer ist."
      ),
      unitTestTestConfig(8, "Die Funktion vector_length soll alle Einträge zuerst quadrieren."),
      unitTestTestConfig(9, "Die Funktion vector_length soll alle quadrierten Einträge addieren."),
      unitTestTestConfig(
        10,
        "Die Funktion vector_length soll die Wurzel der quadrierten und addierten Einträge zurückgeben."
      ),
      unitTestTestConfig(11, "Die Funktion vector_length soll 0 zurückgeben, falls die übergebene Liste leer ist."),
      unitTestTestConfig(
        12,
        "Die Funktion vector_scalar soll den übergebenen Skalarwert zu jedem Element im Originalvektor addieren."
      ),
      unitTestTestConfig(
        13,
        "Die Funktion vector_scalar soll den korrekten übergebenen Skalarwert zu jedem Element im Originalvektor addieren."
      ),
      unitTestTestConfig(
        14,
        "Die Funktion vector_scalar soll eine leere Liste zurückgeben, falls die übergebene Liste leer ist."
      ),
      unitTestTestConfig(
        15,
        "Die Funktion vector_add_vector soll eine leere Liste zurückgeben, falls die Längen der beiden übergebenen Listen nicht übereinstimmen."
      ),
      unitTestTestConfig(16, "Die Funktion vector_add_vector soll die beiden Vektoren korrekt addieren."),
      unitTestTestConfig(
        17,
        "Die Funktion vector_add_vector soll eine leere Liste zurückgeben, falls beide übergebenen Listen leer sind."
      ),
      unitTestTestConfig(18, "Die Funktion flatten_lists soll alle Listen korrekt in einer neuen Liste vereinen."),
      unitTestTestConfig(
        19,
        "Die Funktion flatten_lists soll eine leere Liste zurückgeben, falls die übergebene Liste leer ist."
      )
    ),
    testFileName = "test_general.py",
    folderName = exerciseBaseName,
    sampleSolFileNames = Seq("test_general.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_general.py", fileType, maybeOtherFileName = Some("test_general.py")),
        FileLoadConfig("general.py", fileType, editable = true, Some("general_declaration.py"))
      )
    ),
    implFileName = "general.py",
    sampleSolFileNames = Seq("general.py")
  )

  val programmingColl4Ex3: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
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
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
