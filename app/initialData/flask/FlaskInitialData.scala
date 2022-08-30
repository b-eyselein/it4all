package initialData.flask

import better.files.File
import initialData.flask.FlaskColl01Ex01.flaskColl01Ex01
import initialData.{InitialCollection, InitialData, InitialFilesExerciseContainer}
import model.tools.flask.FlaskExerciseContent

abstract class FlaskInitialExerciseContainer(collectionId: Int, exerciseId: Int) extends InitialFilesExerciseContainer("flask", collectionId, exerciseId) {

  protected val declarationPath: File = exResPath / "declaration"
  protected val solPath: File         = exResPath / "solution"

}

object FlaskInitialData extends InitialData[FlaskExerciseContent] {

  override val initialData = Map(
    1 -> InitialCollection(
      "Beispiel",
      initialExercises = Map(
        1 -> flaskColl01Ex01
      )
    )
  )

}
