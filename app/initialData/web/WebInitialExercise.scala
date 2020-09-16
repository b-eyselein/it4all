package initialData.web

import initialData.InitialFilesExercise

abstract class WebInitialExercise(collectionId: Int, exerciseId: Int)
    extends InitialFilesExercise("web", collectionId, exerciseId) {

  protected val htmlFileType = "htmlmixed"
  protected val cssFileType  = "css"
  protected val jsFileType   = "javascript"

}
