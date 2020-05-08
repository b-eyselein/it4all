package initialData.programming.coll_1

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl1Ex4 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 1, 4)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "fibonacci.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "fibonacci_declaration.py")
      ),
      ExerciseFile(
        name = "test_fibonacci.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_fibonacci_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Diese Implementierun ist korrekt und sollte alle Tests bestehen.",
        file = ExerciseFile(
          name = "fibonacci_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Falls das Argument 'number' keine Zahl ist, soll eine Exception geworfen werden.",
        file = ExerciseFile(
          name = "fibonacci_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description = "Falls das Argument 'number' kleiner als 0 ist, soll eine Exception geworfen werden.",
        file = ExerciseFile(
          name = "fibonacci_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Funktion sollte den richtigen Startwert für 0 benutzen.",
        file = ExerciseFile(
          name = "fibonacci_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion sollte den richtigen Startwert für 1 benutzen.",
        file = ExerciseFile(
          name = "fibonacci_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Die Funktion sollte das richtige Ergebnis berechnen.",
        file = ExerciseFile(
          name = "fibonacci_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "fibonacci_5.py")
        )
      )
    ),
    testFileName = "test_fibonacci.py",
    sampleSolFileNames = Seq("test_fibonacci.py")
  )

  val programmingColl1Ex4: ProgrammingExercise = Exercise(
    exerciseId = 4,
    collectionId = 1,
    toolId = "programming",
    title = "Fibonacci",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Recursion, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      functionName = "fibonacci",
      filename = "fibonacci",
      foldername = "fibonacci",
      inputTypes = Seq(
        ProgInput(id = 1, inputName = "number", inputType = ProgDataTypes.NonGenericProgDataType.INTEGER)
      ),
      outputType = ProgDataTypes.NonGenericProgDataType.INTEGER,
      unitTestPart = unitTestPart,
      implementationPart = ImplementationPart(
        base = "",
        files = Seq(
          ExerciseFile(
            name = "test_fibonacci.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_fibonacci.py")
          ),
          ExerciseFile(
            name = "fibonacci.py",
            fileType,
            editable = true,
            content = loadTextFromFile(exResPath / "fibonacci_declaration.py")
          )
        ),
        implFileName = "fibonacci.py",
        sampleSolFileNames = Seq("fibonacci.py")
      ),
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = ProgSolution(
            files = Seq(
              ExerciseFile(
                name = "test_fibonacci.py",
                fileType,
                editable = false,
                content = loadTextFromFile(exResPath / "test_fibonacci.py")
              ),
              ExerciseFile(
                name = "fibonacci.py",
                fileType,
                editable = false,
                content = loadTextFromFile(exResPath / "fibonacci.py")
              )
            )
          )
        )
      ),
      sampleTestData = Seq.empty
    )
  )

}
