package initialData.programming

import initialData.InitialFilesExercise

abstract class ProgrammingInitialExercise(collectionId: Int, exerciseId: Int)
    extends InitialFilesExercise("programming", collectionId, exerciseId) {

  protected val fileType = "python"

}
