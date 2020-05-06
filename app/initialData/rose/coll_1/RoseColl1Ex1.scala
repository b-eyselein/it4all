package initialData.rose.coll_1

import initialData.InitialData.{ex_resources_path, load_text_from_file}
import model.tools.rose.RoseExerciseContent
import model.tools.rose.RoseTool.RoseExercise
import model.{Exercise, SampleSolution}

object RoseColl1Ex1 {

  private val ex_res_path = ex_resources_path("rose", 1, 1)

  val roseColl1Ex1: RoseExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "rose",
    title = "Zeichnen eines Rechtecks",
    authors = Seq("bje40dc"),
    text = load_text_from_file(ex_res_path / "text.html"),
    difficulty = 1,
    content = RoseExerciseContent(
      fieldWidth = 8,
      fieldHeight = 10,
      isMultiplayer = false,
      inputTypes = Seq(
        /*
            {"id": 1, "name": "width", "type": "int"},
            {"id": 2, "name": "height", "type": "int"}

       */
      ),
      sampleSolutions = Seq(
        SampleSolution(
          id = 1,
          sample = load_text_from_file(ex_res_path / "sol_1" / "robot.py")
        )
      )
    )
  )
}
