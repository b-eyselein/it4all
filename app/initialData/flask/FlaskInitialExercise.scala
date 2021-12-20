package initialData.flask

import better.files.File
import initialData.InitialFilesExercise

abstract class FlaskInitialExercise(collectionId: Int, exerciseId: Int) extends InitialFilesExercise("flask", collectionId, exerciseId) {

  protected val declarationPath: File = exResPath / "declaration"
  protected val solPath: File         = exResPath / "solution"

}
