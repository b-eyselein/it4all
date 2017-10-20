package model

import controllers.xml.XmlToolObject
import model.exercisereading.ExerciseReader

object XmlExerciseReader extends ExerciseReader[XmlExercise]("xml", XmlExercise.finder, classOf[Array[XmlExercise]]) {

//  override def initRemainingExFromForm(exercise: XmlExercise, form: play.data.DynamicForm) {
  //    exercise.exerciseTypeStr = form.get(StringConsts.EXERCISE_TYPE)
  //    exercise.rootNode = form.get(StringConsts.ROOT_NODE_NAME)
  //  }

  override def instantiate(id: Int): XmlExercise = {
    val ex = new XmlExercise
    ex.id = id
    ex
  }

  override def save(exercise: XmlExercise): Unit = exercise.save()

  override def updateExercise(exercise: XmlExercise, exerciseNode: com.fasterxml.jackson.databind.JsonNode) {
    exercise.exerciseTypeStr = exerciseNode.get(StringConsts.EXERCISE_TYPE).asText
    exercise.rootNode = exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText
  }

  override def checkFiles(exercise: XmlExercise): List[scala.util.Try[java.nio.file.Path]] = {
    List(checkOrCreateSampleFile(exercise, XmlToolObject, exercise.rootNode + "." + exercise.exerciseType.refFileEnding))
  }

}
