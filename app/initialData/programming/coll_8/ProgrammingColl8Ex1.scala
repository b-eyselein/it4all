package initialData.programming.coll_8

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl8Ex1 extends ProgrammingInitialExercise(8, 1, "table") {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = loadTextFromFile(exResPath / "unit_test_description.html"),
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("table.py", fileType, maybeOtherFileName = Some("table_declaration.py")),
        FileLoadConfig("raum.csv", fileType),
        FileLoadConfig("test_table.py", fileType, editable = true, Some("test_table_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung", shouldFail = false),
      unitTestTestConfig(
        1,
        "Es soll überprüft werden, ob die eingegebene Liste die gleiche Länge wie die Tabellenreihen hat."
      ),
      unitTestTestConfig(
        2,
        "Es soll überprüft werden, ob der Inhalt der eingegebenen Liste die gleichen Datentypen hat wie die Anfangsdaten."
      ),
      unitTestTestConfig(3, "Die Funktion sollte bei Erfolg True ausgeben."),
      unitTestTestConfig(4, "Die letzte Zeile der Tabelle muss der angefügten Zeile entsprechen."),
      unitTestTestConfig(5, "Die neue Anzahl an Zeilen muss um genau 1 höher sein."),
      unitTestTestConfig(6, "Zahlen in der neuen Zeile sollen automatisch von Strings zu floats konvertiert werden.")
    ),
    testFileName = "test_table.py",
    folderName = "pysql",
    sampleSolFileNames = Seq("test_table.py")
  )

  private val implementationPart = ImplementationPart(
    base = "",
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_table.py", fileType, maybeOtherFileName = Some("test_table_declaration.py")),
        FileLoadConfig("table.py", fileType, editable = true, Some("table_declaration.py")),
        FileLoadConfig("raum.csv", fileType)
      )
    ),
    implFileName = "table.py",
    sampleSolFileNames = Seq("table.py")
  )

  val programmingColl8Ex1: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "PySQL",
    authors = Seq("amh12ry"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Classes, Level.Intermediate),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 4,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
