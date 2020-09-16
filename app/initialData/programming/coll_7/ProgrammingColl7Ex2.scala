package initialData.programming.coll_7

import initialData.FileLoadConfig
import initialData.InitialData._
import initialData.programming.ProgrammingInitialExercise
import model._
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._

object ProgrammingColl7Ex2 extends ProgrammingInitialExercise(7, 2) {

  private val unitTestPart = NormalUnitTestPart(
    unitTestsDescription = "",
    unitTestFiles = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("maumau.py", fileType, maybeOtherFileName = Some("maumau_declaration.py")),
        FileLoadConfig("test_maumau.py", fileType, editable = true, Some("test_maumau_declaration.py"))
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "maumau_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Die Funktion can_be_played_on soll ebenfalls true zurückgeben, falls die Bilder der beiden Karten übereinstimmen.",
        file = ExerciseFile(
          name = "maumau_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion can_be_played_on soll ebenfalls true zurückgeben, falls die Farben der beiden Karten übereinstimmen.",
        file = ExerciseFile(
          name = "maumau_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Die Funktion can_be_played_on soll nur true zurückgeben, falls die Farben der beiden Karten übereinstimmen.",
        file = ExerciseFile(
          name = "maumau_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description =
          "Die Funktion can_be_played_on soll nur true zurückgeben, falls die Bilder der beiden Karten übereinstimmen.",
        file = ExerciseFile(
          name = "maumau_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Die Funktion can_be_palyed_on soll true zurückgeben, falls entweder die Farben oder die Bilder der beiden Karten übereinstimmen.",
        file = ExerciseFile(
          name = "maumau_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description = "Die Funktion playable_cards soll die Handkarten zurückgeben, die spielbar sind.",
        file = ExerciseFile(
          name = "maumau_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_6.py")
        )
      ),
      UnitTestTestConfig(
        id = 7,
        shouldFail = true,
        description = "Die Funktion playable_cards soll alle Handkarten zurückgeben, die spielbar sind.",
        file = ExerciseFile(
          name = "maumau_7.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_7.py")
        )
      ),
      UnitTestTestConfig(
        id = 8,
        shouldFail = true,
        description =
          "Die Funktion playable_cards soll eine leere Liste zurückgeben, falls keine Handkarten vorhanden sind.",
        file = ExerciseFile(
          name = "maumau_8.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "maumau_8.py")
        )
      )
    ),
    testFileName = "test_maumau.py",
    folderName = "maumau",
    sampleSolFileNames = Seq("test_maumau.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig("test_maumau.py", fileType),
        FileLoadConfig("maumau.py", fileType, editable = true, Some("maumau_declaration.py"))
      )
    ),
    implFileName = "maumau.py",
    sampleSolFileNames = Seq("maumau.py")
  )

  private val sampleSolutionFiles = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig("maumau.py", fileType),
      FileLoadConfig("test_maumau.py", fileType)
    )
  )

  private val sampleSolutions = Seq(SampleSolution(1, FilesSolution(sampleSolutionFiles)))

  val programmingColl7Ex2: ProgrammingExercise = Exercise(
    exerciseId,
    collectionId,
    toolId,
    title = "Mau-Mau",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Classes, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Strings, Level.Beginner)
    ),
    difficulty = 1,
    content = ProgrammingExerciseContent(
      filename = "maumau",
      unitTestPart,
      implementationPart,
      sampleSolutions
    )
  )

}
