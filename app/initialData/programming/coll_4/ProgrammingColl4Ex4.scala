package initialData.programming.coll_4

import initialData.InitialData._
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl4Ex4 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 4, 4)

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "slicing.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "slicing_declaration.py")
      ),
      ExerciseFile(
        name = "test_slicing.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_slicing_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "slicing_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description = "Die Funktion even_indexes soll alle Elemente an geraden Indizes zurückgeben.",
        file = ExerciseFile(
          name = "slicing_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion even_indexes soll alle Elemente an geraden Indizes und nicht an ungeraden Indizes zurückgeben.",
        file = ExerciseFile(
          name = "slicing_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Die Funktion even_indexes soll alle Elemente an gerade Indizes und nicht am 0. Index zurückgeben.",
        file = ExerciseFile(
          name = "slicing_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description = "Die Funktion reversed_special soll vom vorletzten Element aus beginnen.",
        file = ExerciseFile(
          name = "slicing_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Die Funktion reversed_special soll jedes dritte Element, vom vorletzten Element aus beginnend, ausgeben.",
        file = ExerciseFile(
          name = "slicing_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Die Funktion first_half soll die erste Hälfte der Liste zurückgeben.",
        file = ExerciseFile(
          name = "slicing_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Die Funktion first_half soll bei ungeraden Längen abrunden.",
        file = ExerciseFile(
          name = "slicing_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description =
          "Die Funktion rotate_right soll für jede Umdrehung das letzte Element der Liste an den Anfang der Liste setzen.",
        file = ExerciseFile(
          name = "slicing_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_8.py")
        )
      ),
      UnitTestTestConfig(
        id = 9,
        shouldFail = true,
        description = "Die Funktion rotate_right soll die korrekte Anzahl an übergebenen Umdrehungen durchführen.",
        file = ExerciseFile(
          name = "slicing_9.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "slicing_9.py")
        )
      )
    ),
    testFileName = "test_slicing.py",
    folderName = "slicing",
    sampleSolFileNames = Seq("test_slicing.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_slicing.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_slicing.py")
      ),
      ExerciseFile(
        name = "slicing.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "slicing_declaration.py")
      )
    ),
    implFileName = "slicing.py",
    sampleSolFileNames = Seq("slicing.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = FilesSolution(
        files = Seq(
          ExerciseFile(
            name = "slicing.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "slicing.py")
          ),
          ExerciseFile(
            name = "test_slicing.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_slicing.py")
          )
        )
      )
    )
  )

  val programmingColl4Ex4: ProgrammingExercise = Exercise(
    exerciseId = 4,
    collectionId = 4,
    toolId,
    title = "Slicing",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
    ),
    difficulty = 2,
    content = ProgrammingExerciseContent(
      filename = "slicing",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
