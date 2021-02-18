package initialData.programming.coll_2

import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl2Ex6 extends ProgrammingInitialExercise(2, 6, "xmas_tree") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, """Die Funktion xmas_tree_top_simple soll die Ränder mit einer \# kennzeichnen."""),
      unitTestTestConfig(
        2,
        """Die Funktion xmas_tree_top_simple soll den Zwischenraum (kein Rand und kein Weihnachtsbaum)
          |durch die korrekte Anzahl an Leerzeichen füllen.""".stripMargin
      ),
      unitTestTestConfig(
        3,
        "Die Funktion xmas_tree_top_simple soll den Weihnachtsbaum durch die korrekte Anzahl an * kennzeichnen."
      ),
      unitTestTestConfig(4, """Die Funktion xmas_tree_top_design soll die Ränder mit einer \# kennzeichnen"""),
      unitTestTestConfig(
        5,
        """Die Funktion xmas_tree_top_design soll den Zwischenraum (kein Rand und kein Weihnachtsbaum)
          |durch die korrekte Anzahl an Leerzeichen füllen.""".stripMargin
      ),
      unitTestTestConfig(
        6,
        "Die Funktion xmas_tree_top_design soll den Weihnachtsbaum durch die korrekte Anzahl an * kennzeichnen."
      ),
      unitTestTestConfig(
        7,
        "Die Funktion xmas_tree_top_design soll die korrekte Anzahl Dekoration in den Weihnachtsbaum einfügen."
      ),
      unitTestTestConfig(8, """Die Funktion xmas_tree_stub soll die Ränder mit einer \# kennzeichnen."""),
      unitTestTestConfig(
        9,
        "Die Funktion xmas_tree_stub soll den Zwischenraum (kein Rand und kein Stumpf) durch die korrekte Anzahl an Leerzeichen füllen."
      ),
      unitTestTestConfig(
        10,
        "Die Funktion xmas_tree_stub soll den Stumpf durch die korrekte Anzahl an I kennzeichnen."
      ),
      unitTestTestConfig(
        11,
        "Die Funktion xmas_tree_simple soll den korrekten Weihnachtsbaum ohne Dekoration erstellen."
      ),
      unitTestTestConfig(
        12,
        "Die Funktion xmas_tree_simple soll den korrekten Weihnachtsbaum ohne Dekoration erstellen."
      ),
      unitTestTestConfig(
        13,
        "Die Funktion xmas_tree_design soll den korrekten Weihnachtsbaum mit Dekoration erstellen."
      ),
      unitTestTestConfig(
        14,
        "Die Funktion xmas_tree_design soll den korrekten Weihnachtsbaum mit Dekoration erstellen."
      )
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl2Ex6: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Weihnachtsbaum",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
