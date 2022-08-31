package initialData.programming.coll_1

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl1Ex2 extends ProgrammingInitialExerciseContainer(1, 2, "factorial") {

  private val unitTestPart = UnitTestPart(
    unitTestsDescription = """Schreiben Sie Unittests für die Funktion <code>factorial(n= int) -> int</code>.
                             |Diese soll die Fakultät der Zahl <code>n</code> berechnen.
                             |Der Funktionsparameter <code>n</code> soll größer als 0 sein.""".stripMargin
      .replace("\n", " "),
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(
        0,
        description = "Diese Implementierung ist korrekt und sollte alle Tests bestehen.",
        shouldFail = false
      ),
      unitTestTestConfig(1, "Falls das Funktionsargument 'n' keine Ganzzahl ist, soll eine Exception geworfen werden."),
      unitTestTestConfig(
        2,
        "Falls das Funktionsargument kleiner oder gleich 0 ist, soll eine Exception geworfen werden."
      ),
      unitTestTestConfig(3, "Die Funktion soll das richtige Resultat zurückliefern.")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl1Ex2: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Fakultät",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = Level.Beginner,
    topicsWithLevels = Map(
      ProgrammingTopics.Exceptions -> Level.Beginner,
      ProgrammingTopics.Maths      -> Level.Beginner,
      ProgrammingTopics.ForLoops   -> Level.Beginner
    ),
    content = ProgrammingExerciseContent(
      filename = exerciseBaseName,
      unitTestPart,
      defaultImplementationPart,
      Seq(FilesSolution(defaultSampleSolutionFiles))
    )
  )

}
