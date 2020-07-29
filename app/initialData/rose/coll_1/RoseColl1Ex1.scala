package initialData.rose.coll_1

import initialData.InitialData._
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise
import model.{Exercise, SampleSolution}

object RoseColl1Ex1 {

  private val exResPath = exerciseResourcesPath("rose", 1, 1)

  val roseColl1Ex1: RoseExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "rose",
    title = "Zeichnen eines Rechtecks",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(exResPath / "text.html"),
    difficulty = 1,
    content = RoseExerciseContent(
      fieldWidth = 8,
      fieldHeight = 10,
      isMultiplayer = false,
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = loadTextFromFile(exResPath / "sol_1" / "robot.py")
        )
      )
    )
  )
}
