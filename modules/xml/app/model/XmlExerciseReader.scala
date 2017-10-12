package model

object XmlExerciseReader extends model.exercisereading.ExerciseReader[XmlExercise](
  "xml", XmlExercise.finder, classOf[Array[XmlExercise]]) {

  override def initRemainingExFromForm(exercise: XmlExercise, form: play.data.DynamicForm) {
    exercise.exerciseTypeStr = form.get(StringConsts.EXERCISE_TYPE)
    exercise.rootNode = form.get(StringConsts.ROOT_NODE_NAME)

    //    val referenceFilePath = Paths.get(XmlToolObject.sampleDir.toString, exercise.rootNode + "." + exercise.exerciseType.refFileEnding)
    //    val referenceFileContent = form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE).toList.asJava
    //
    //    Try(Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.TRUNCATE_EXISTING)) match {
    //      case Success(_) => Unit
    //      case Failure(e) => Logger.error(s"Es gab einen Fehler beim Erstellen der Referenzdatei $referenceFilePath", e)
    //    }
  }

  override def instantiate(id: Int) = new XmlExercise(id)

  override def save(exercise: XmlExercise) {
    exercise.save()
  }

  override def updateExercise(exercise: XmlExercise, exerciseNode: com.fasterxml.jackson.databind.JsonNode) {
    exercise.exerciseTypeStr = exerciseNode.get(StringConsts.EXERCISE_TYPE).asText
    exercise.rootNode = exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText
  }

  override def checkFiles(exercise: XmlExercise): List[scala.util.Try[java.nio.file.Path]] = {
    List(checkOrCreateSampleFile(exercise, controllers.xml.XmlToolObject, exercise.rootNode + "." + exercise.exerciseType.refFileEnding))
  }

}
