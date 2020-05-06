package initialData.rose.coll_1

import initialData.InitialData._
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise
import model.{Exercise, SampleSolution}

object RoseColl1Ex2 {

  private val ex_res_path = exerciseResourcesPath("rose", 1, 2)

  val roseColl1Ex2: RoseExercise = Exercise(
    exerciseId = 2,
    collectionId = 1,
    toolId = "rose",
    title = "Kaninchenjagd",
    authors = Seq("bje40dc"),
    text = loadTextFromFile(ex_res_path / "text.html"),
    difficulty = 1,
    content = RoseExerciseContent(
      fieldWidth = 8,
      fieldHeight = 10,
      isMultiplayer = false,
      inputTypes = Seq(
        /*
            {"id": 1, "name": "rabbit_x", "type": "int"},
            {"id": 2, "name": "rabbit_y", "type": "int"}
       */
      ),
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = loadTextFromFile(ex_res_path / "sol_1" / "robot.py")
        )
      )
    )
  )

}
