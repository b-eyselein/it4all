package initialData.programming.coll_8

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl8Ex1 extends ProgrammingInitialExercise(8, 1) {

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
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung",
        file = ExerciseFile(
          name = "table_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Es soll überprüft werden, ob die eingegebene Liste die gleiche Länge wie die Tabellenreihen hat.",
        file = ExerciseFile(
          name = "table_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Es soll überprüft werden, ob der Inhalt der eingegebenen Liste die gleichen Datentypen hat wie die Anfangsdaten.",
        file = ExerciseFile(
          name = "table_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion sollte bei Erfolg True ausgeben.",
        file = ExerciseFile(
          name = "table_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die letzte Zeile der Tabelle muss der angefügten Zeile entsprechen.",
        file = ExerciseFile(
          name = "table_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die neue Anzahl an Zeilen muss um genau 1 höher sein.",
        file = ExerciseFile(
          name = "table_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Zahlen in der neuen Zeile sollen automatisch von Strings zu floats konvertiert werden.",
        file = ExerciseFile(
          name = "table_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "table_6.py")
        )
      )
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

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("table.py", fileType),
      FileLoadConfig("test_table.py", fileType)
    )
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
      filename = "table",
      unitTestPart,
      implementationPart,
      Seq(FilesSolution(sampleSolutionFiles))
    )
  )

}
