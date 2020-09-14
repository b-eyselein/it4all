package initialData.programming.coll_7

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl7Ex1 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 7, 1)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = loadTextFromFile(exResPath / "unit_tests_description.html"),
    unitTestFiles = Seq(
      ExerciseFile(
        name = "circle.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "circle_declaration.py")
      ),
      ExerciseFile(
        name = "test_circle.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_circle_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "circle_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die X-Koordinate sollte eine Ganz- oder Fließkommazahl sein.",
        file = ExerciseFile(
          name = "circle_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Der Wert des Konstruktorarguments 'center_x' sollte unter dem selben Namen als Argument zugänglich sein.",
        file = ExerciseFile(
          name = "circle_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description = "Die Y-Koordinate sollte eine Ganz- oder Fließkommazahl sein.",
        file = ExerciseFile(
          name = "circle_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description =
          "Der Wert des Konstruktorarguments 'center_y' sollte unter dem selben Namen als Argument zugänglich sein.",
        file = ExerciseFile(
          name = "circle_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description = "Der Radius sollte eine Ganz- oder Fließkommazahl sein.",
        file = ExerciseFile(
          name = "circle_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Der Radius sollte größer oder gleich 0 sein.",
        file = ExerciseFile(
          name = "circle_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description =
          "Der Wert des Konstruktorarguments 'radius' sollte unter dem selben Namen als Argument zugänglich sein.",
        file = ExerciseFile(
          name = "circle_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description = "Die Fläche des Kreises sollte richtig berechnet werden",
        file = ExerciseFile(
          name = "circle_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "circle_8.py")
        )
      )
    ),
    testFileName = "test_circle.py",
    folderName = "circle",
    sampleSolFileNames = Seq("test_circle.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_circle.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_circle.py")
      ),
      ExerciseFile(
        name = "circle.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "circle_declaration.py")
      )
    ),
    implFileName = "circle.py",
    sampleSolFileNames = Seq("circle.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = FilesSolution(
        files = Seq(
          ExerciseFile(
            name = "circle.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "circle.py")
          ),
          ExerciseFile(
            name = "test_circle.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_circle.py")
          )
        )
      )
    )
  )

  val programmingColl7Ex1: ProgrammingExercise = Exercise(
    exerciseId = 1,
    collectionId = 7,
    toolId,
    title = "Kreise",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Exceptions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Classes, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Maths, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "circle",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
