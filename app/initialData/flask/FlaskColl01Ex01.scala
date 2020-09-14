package initialData.flask

import model.tools.flask.FlaskExerciseContent
import model.tools.flask.FlaskTool.FlaskExercise
import model.{Exercise, FilesSolution, SampleSolution}

object FlaskColl01Ex01 {

  private val sampleSolutions: Seq[SampleSolution[FilesSolution]] = Seq.empty

  val flaskColl01Ex01: FlaskExercise = Exercise(
    exerciseId = 1,
    collectionId = 1,
    toolId = "flask",
    title = "Testaufgabe",
    authors = Seq("bje40dc"),
    text = "TODO!",
    difficulty = 1,
    content = FlaskExerciseContent(
      sampleSolutions
    )
  )

}
