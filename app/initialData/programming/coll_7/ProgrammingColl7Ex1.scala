package initialData.programming.coll_7

import initialData.InitialData._
import initialData.InitialExercise
import initialData.programming.ProgrammingInitialExerciseContainer
import model._
import model.tools.programming._

object ProgrammingColl7Ex1 extends ProgrammingInitialExerciseContainer(7, 1, "circle") {

  private val unitTestPart = UnitTestPart(
    unitTestsDescription = loadTextFromFile(exResPath / "unit_tests_description.html"),
    unitTestFiles = unitTestFiles,
    unitTestTestConfigs = Seq(
      unitTestTestConfig(0, "Musterlösung...", shouldFail = false),
      unitTestTestConfig(1, "Die X-Koordinate sollte eine Ganz- oder Fließkommazahl sein."),
      unitTestTestConfig(
        2,
        "Der Wert des Konstruktorarguments 'center_x' sollte unter dem selben Namen als Argument zugänglich sein."
      ),
      unitTestTestConfig(3, "Die Y-Koordinate sollte eine Ganz- oder Fließkommazahl sein."),
      unitTestTestConfig(
        4,
        "Der Wert des Konstruktorarguments 'center_y' sollte unter dem selben Namen als Argument zugänglich sein."
      ),
      unitTestTestConfig(5, "Der Radius sollte eine Ganz- oder Fließkommazahl sein."),
      unitTestTestConfig(6, "Der Radius sollte größer oder gleich 0 sein."),
      unitTestTestConfig(
        7,
        "Der Wert des Konstruktorarguments 'radius' sollte unter dem selben Namen als Argument zugänglich sein."
      ),
      unitTestTestConfig(8, "Die Fläche des Kreises sollte richtig berechnet werden")
    ),
    testFileName = testFileName,
    folderName = exerciseBaseName
  )

  val programmingColl7Ex1: InitialExercise[ProgrammingExerciseContent] = InitialExercise(
    title = "Kreise",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    topicsWithLevels = Map(
      ProgrammingTopics.Conditions -> Level.Beginner,
      ProgrammingTopics.Exceptions -> Level.Beginner,
      ProgrammingTopics.Classes    -> Level.Beginner,
      ProgrammingTopics.Maths      -> Level.Beginner
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
