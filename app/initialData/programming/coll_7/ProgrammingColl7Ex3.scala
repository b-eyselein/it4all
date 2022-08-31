package initialData.programming.coll_7

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl7Ex3 extends ProgrammingInitialExerciseContainer(7, 3, "vector2d") {

  private val unitTestPart = UnitTestPart(
    // FIXME: unit tests description!
    unitTestsDescription = "TODO!",
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(
        1,
        "Der Wert des Konstruktorarguments 'x' sollte unter dem selben Namen als Argument zugänglich sein."
      ),
      unitTestTestConfig(
        2,
        "Der Wert des Konstruktorarguments 'y' sollte unter dem selben Namen als Argument zugänglich sein."
      ),
      unitTestTestConfig(
        3,
        "Die Funktion __repr__ soll die korrekte Koordinatenrepräsentation der Koordinate 'x' zurückgeben."
      ),
      unitTestTestConfig(
        4,
        "Die Funktion __repr__ soll die korrekte Koordinatenrepräsentation der Koordinate 'y' zurückgeben."
      ),
      unitTestTestConfig(
        5,
        "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls sich zwei Vektoren gleichen."
      ),
      unitTestTestConfig(
        6,
        "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls sich zwei Vektoren nicht gleichen."
      ),
      unitTestTestConfig(
        7,
        "Die Funktion __eq__ soll den korrekten booleschen Wert zurückgeben, falls der Vektor2D mit einer Instanz verglichen wird, die kein Vektor2D ist."
      ),
      unitTestTestConfig(8, "Die Funktion abs soll die korrekte Länge berechnen."),
      unitTestTestConfig(9, "Die Funktion abs soll die korrekte Länge berechnen."),
      unitTestTestConfig(10, "Die Funktion __add__ soll jeweils die x- und y-Koordinaten addieren."),
      unitTestTestConfig(12, "Die Funktion __sub__ soll jeweils die x- und y-Koordinaten subtrahieren."),
      unitTestTestConfig(
        13,
        "Die Funktion __sub__ soll den korrekt berechneten Vektor nach der Subtraktion zurückgeben."
      ),
      unitTestTestConfig(
        14,
        "Die Funktion __mul__ soll jeweils die x- und y-Koordinaten mit dem übergebenen Wert multiplizieren."
      ),
      unitTestTestConfig(
        15,
        "Die Funktion __mul__ soll den korrekt berechneten Vektor nach der Multiplikation zurückgeben."
      ),
      unitTestTestConfig(
        16,
        "Die Funktion dot soll jeweils die x- und y-Koordinaten multiplizieren und anschließend das Ergebnis addieren."
      ),
      unitTestTestConfig(17, "Die Funktion dot soll das korrekt berechnete Skalarprodukt zurückgeben.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl7Ex3: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Vektor2D",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Classes -> Level.Beginner,
      ProgrammingTopics.Strings -> Level.Beginner,
      ProgrammingTopics.Maths   -> Level.Beginner
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
