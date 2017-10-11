package model

import java.nio.file.Path

import com.fasterxml.jackson.databind.JsonNode
import model.exercisereading.ExerciseReader
import play.data.DynamicForm

import scala.util.Try

object XmlExerciseReader extends ExerciseReader[XmlExercise]("xml", XmlExercise.finder, classOf[Array[XmlExercise]]) {

  override def initRemainingExFromForm(exercise: XmlExercise, form: DynamicForm) {
    exercise.exerciseTypeStr = form.get(StringConsts.EXERCISE_TYPE)
    exercise.rootNode = form.get(StringConsts.ROOT_NODE_NAME)

    //    val referenceFilePath = Paths.get(
    //      BaseController.getSampleDir(exerciseType).toString,
    //      exercise.rootNode + "." + exercise.getReferenceFileEnding
    //    )
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

  override def updateExercise(exercise: XmlExercise, exerciseNode: JsonNode) {
    exercise.exerciseTypeStr = exerciseNode.get(StringConsts.EXERCISE_TYPE).asText
    exercise.rootNode = exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText
  }

  override def checkFiles(exercise: XmlExercise): List[Try[Path]] = {
    //    val fileName = exercise.rootNode + "." + exercise.getReferenceFileEnding
    //    List(checkOrCreateSampleFile(exercise, toolObject, fileName))
    // FIXME: Files...
    List.empty
  }

}
