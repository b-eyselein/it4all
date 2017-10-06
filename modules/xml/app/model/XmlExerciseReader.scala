package model

import java.nio.file.{ Files, Paths, StandardOpenOption }

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.util.{ Failure, Success, Try }

import com.fasterxml.jackson.databind.JsonNode

import controllers.core.BaseController
import model.exercisereading.ExerciseReader
import play.Logger
import play.data.DynamicForm

object XmlExerciseReader extends ExerciseReader[XmlExercise]("xml", XmlExercise.finder, classOf[Array[XmlExercise]]) {

  override def initRemainingExFromForm(exercise: XmlExercise, form: DynamicForm) {
    exercise.exerciseType = XmlExType.valueOf(form.get(StringConsts.EXERCISE_TYPE))
    exercise.rootNode = form.get(StringConsts.ROOT_NODE_NAME)

    val referenceFilePath = Paths.get(
      BaseController.getSampleDir(exerciseType).toString,
      exercise.rootNode + "." + exercise.getReferenceFileEnding
    )
    val referenceFileContent = form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE).toList.asJava

    Try(Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.TRUNCATE_EXISTING)) match {
      case Success(_) ⇒ Unit
      case Failure(e) ⇒ Logger.error(s"Es gab einen Fehler beim Erstellen der Referenzdatei $referenceFilePath", e)
    }
  }

  override def instantiate(id: Int) = new XmlExercise(id)

  override def save(exercise: XmlExercise) {
    exercise.save
    val fileName = exercise.rootNode + "." + exercise.getReferenceFileEnding
    checkOrCreateSampleFile(exercise, fileName) match {
      case Failure(e) => Logger.error(s"An error has occured while saving the sample file $fileName", e)
      case Success(s) => Logger.info(s"Solution file $fileName was created successfully")
    }
  }

  override def updateExercise(exercise: XmlExercise, exerciseNode: JsonNode) {
    exercise.exerciseType = XmlExType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText)
    exercise.rootNode = exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText
  }

}
