package initialData.flask

import initialData.InitialData
import initialData.flask.FlaskColl01Ex01.flaskColl01Ex01
import model.tools.flask.FlaskExerciseContent
import model.{Exercise, ExerciseCollection}

object FlaskInitialData extends InitialData[FlaskExerciseContent] {

  override protected val toolId: String = "flask"

  private val flaskColl01 = ExerciseCollection(1, toolId, "Beispiel", Seq("bje40dc"))

  private val flaskColl01Exes: Seq[Exercise[FlaskExerciseContent]] = Seq(
    flaskColl01Ex01
  )

  override val exerciseData: Seq[(ExerciseCollection, Seq[Exercise[FlaskExerciseContent]])] = Seq(
    (flaskColl01, flaskColl01Exes)
  )

}
