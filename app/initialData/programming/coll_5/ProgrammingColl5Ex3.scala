package initialData.programming.coll_5

import initialData.InitialData.{exerciseResourcesPath, loadTextFromFile}
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import model.tools.programming._
import model._

object ProgrammingColl5Ex3 {

  private val toolId = "programming"

  private val fileType = "python"

  private val exResPath = exerciseResourcesPath(toolId, 5, 3)

  private val unitTestPart = UnitTestPart(
    unitTestType = UnitTestType.Normal,
    unitTestsDescription = "",
    unitTestFiles = Seq(
      ExerciseFile(
        name = "tuples_and_dicts.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "tuples_and_dicts_declaration.py")
      ),
      ExerciseFile(
        name = "test_tuples_and_dicts.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "test_tuples_and_dicts_declaration.py")
      )
    ),
    unitTestTestConfigs = Seq(
      UnitTestTestConfig(
        id = 0,
        shouldFail = false,
        description = "Musterlösung...",
        file = ExerciseFile(
          name = "tuples_and_dicts_0.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_0.py")
        )
      ),
      UnitTestTestConfig(
        id = 1,
        shouldFail = true,
        description =
          "Die Funktion tuple_list_to_dict soll den ersten Eintrag im Tupel als Schlüssel und den zweiten Eintrag als Wert setzen.",
        file = ExerciseFile(
          name = "tuples_and_dicts_1.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_1.py")
        )
      ),
      UnitTestTestConfig(
        id = 2,
        shouldFail = true,
        description =
          "Die Funktion tuple_list_to_dict soll den ersten Wert verwenden, falls ein Schlüssel mehrmals vorkommt.",
        file = ExerciseFile(
          name = "tuples_and_dicts_2.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_2.py")
        )
      ),
      UnitTestTestConfig(
        id = 3,
        shouldFail = true,
        description =
          "Die Funktion tuple_list_to_dict soll ein leeres Dictionary zurückgeben, falls eine leere Liste übergeben wird.",
        file = ExerciseFile(
          name = "tuples_and_dicts_3.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_3.py")
        )
      ),
      UnitTestTestConfig(
        id = 4,
        shouldFail = true,
        description =
          "Die Funktion intersect_dicts soll ein leeres Dictionary zurückgeben, falls keine übereinstimmenden Schlüsseln in den übergebenen Dictionaries existieren.",
        file = ExerciseFile(
          name = "tuples_and_dicts_4.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_4.py")
        )
      ),
      UnitTestTestConfig(
        id = 5,
        shouldFail = true,
        description =
          "Die Funktion intersect_dicts soll ein Dictionary mit den korrekten Werten bei übereinstimmenden Schlüsseln zurückgeben.",
        file = ExerciseFile(
          name = "tuples_and_dicts_5.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_5.py")
        )
      ),
      UnitTestTestConfig(
        id = 6,
        shouldFail = true,
        description =
          "Die Funktion intersect_dicts soll ein leeres Dictionary zurückgeben, falls die beiden übergebenen Dictionaries leer sind.",
        file = ExerciseFile(
          name = "tuples_and_dicts_6.py",
          fileType,
          editable = false,
          content = loadTextFromFile(exResPath / "unit_test_sols" / "tuples_and_dicts_6.py")
        )
      )
    ),
    testFileName = "test_tuples_and_dicts.py",
    sampleSolFileNames = Seq("test_tuples_and_dicts.py")
  )

  private val implementationPart = ImplementationPart(
    base = loadTextFromFile(exResPath / "base.py"),
    files = Seq(
      ExerciseFile(
        name = "test_tuples_and_dicts.py",
        fileType,
        editable = false,
        content = loadTextFromFile(exResPath / "test_tuples_and_dicts.py")
      ),
      ExerciseFile(
        name = "tuples_and_dicts.py",
        fileType,
        editable = true,
        content = loadTextFromFile(exResPath / "tuples_and_dicts_declaration.py")
      )
    ),
    implFileName = "tuples_and_dicts.py",
    sampleSolFileNames = Seq("tuples_and_dicts.py")
  )

  private val sampleSolutions = Seq(
    SampleSolution(
      id = 1,
      sample = ProgSolution(
        files = Seq(
          ExerciseFile(
            name = "tuples_and_dicts.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "tuples_and_dicts.py")
          ),
          ExerciseFile(
            name = "test_tuples_and_dicts.py",
            fileType,
            editable = false,
            content = loadTextFromFile(exResPath / "test_tuples_and_dicts.py")
          )
        )
      )
    )
  )

  val programmingColl5Ex3: ProgrammingExercise = Exercise(
    exerciseId = 3,
    collectionId = 5,
    toolId,
    title = "Tupel und Dictionaries",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Seq(
      TopicWithLevel(ProgrammingTopics.Lists, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Tuples, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Dicts, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.ForLoops, Level.Beginner),
      TopicWithLevel(ProgrammingTopics.Conditions, Level.Beginner)
    ),
    difficulty = 3,
    content = ProgrammingExerciseContent(
      functionName = "tuples_and_dicts",
      foldername = "tuples_and_dicts",
      filename = "tuples_and_dicts",
      inputTypes = Seq.empty,
      outputType = ProgDataTypes.NonGenericProgDataType.VOID,
      unitTestPart,
      implementationPart,
      sampleTestData = Seq.empty,
      sampleSolutions
    )
  )

}
