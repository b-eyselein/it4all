package initialData.rose.coll_1

import initialData.InitialData._
import model.Exercise
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise

object RoseColl1Ex2 {

  private val exResPath = exerciseResourcesPath("rose", 1, 2)

  val roseColl1Ex2: RoseExercise = Exercise(
    exerciseId = 2,
    collectionId = 1,
    toolId = "rose",
    title = "Kaninchenjagd",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = RoseExerciseContent(
      fieldWidth = 8,
      fieldHeight = 10,
      isMultiplayer = false,
      sampleSolutions = Seq(
        loadTextFromFile(exResPath / "sol_1" / "robot.py")
      )
    )
  )

}
