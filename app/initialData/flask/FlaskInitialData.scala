package initialData.flask

import initialData.flask.FlaskColl01Ex01.flaskColl01Ex01
import initialData.{InitialCollection, InitialData}
import model.tools.flask.FlaskExerciseContent

object FlaskInitialData extends InitialData[FlaskExerciseContent] {

  override val initialData = Seq(
    InitialCollection(
      collectionId = 1,
      "Beispiel",
      Seq("bje40dc"),
      exercises = Seq(flaskColl01Ex01)
    )
  )

}
