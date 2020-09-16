package initialData.programming.coll_1

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex2 extends ProgrammingInitialExercise(1, 2) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = """Schreiben Sie Unittests für die Funktion <code>factorial(n= int) -> int</code>.
                             |Diese soll die Fakultät der Zahl <code>n</code> berechnen.
                             |Der Funktionsparameter <code>n</code> soll größer als 0 sein.""".stripMargin
      .replace("\n", " "),
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("factorial.py", fileType, editable = false, Some("factorial_declaration.py")),
        FileLoadConfig("test_factorial.py", fileType, editable = true, Some("test_factorial_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Diese Implementierung ist korrekt und sollte alle Tests bestehen.",
        file = ExerciseFile(
          name = "factorial_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "factorial_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = """Falls das Funktionsargument 'n' keine Ganzzahl ist, soll eine Exception geworfen werden.""",
        file = ExerciseFile(
          name = "factorial_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "factorial_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Falls das Funktionsargument kleiner oder gleich 0 ist, soll eine Exception geworfen werden.",
        file = ExerciseFile(
          name = "factorial_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "factorial_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion soll das richtige Resultat zurückliefern.",
        file = ExerciseFile(
          name = "factorial_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "factorial_3.py")
        )
      )
    ),
    testFileName = "test_factorial.py",
    folderName = "factorial",
    sampleSolFileNames = Seq("test_factorial.py")
  )

  private val implementationPart = ImplementationPart(
    base = """def factorial(n: int) -> int:
             |    pass
             |""".stripMargin,
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_factorial.py", fileType),
        FileLoadConfig("factorial.py", fileType, editable = true, Some("factorial_declaration.py"))
      )
    ),
    implFileName = "factorial.py",
    sampleSolFileNames = Seq("factorial.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("test_factorial.py", fileType),
      FileLoadConfig("factorial.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl1Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Fakultät",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner)
    ),
    content = ProgrammingExerciseContent(
      filename = "factorial",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
