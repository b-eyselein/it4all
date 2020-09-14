package initialData.flask

import initialData.InitialData
import model.tools.flask.FlaskExerciseContent
import model.{Exercise, ExerciseCollection}

object FlaskInitialData extends InitialData[FlaskExerciseContent] {

  override protected val toolId: String = "flask"

  override val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[FlaskExerciseContent]])] = Seq.empty

}
