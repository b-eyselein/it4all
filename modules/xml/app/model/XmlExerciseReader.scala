package model

import java.nio.file.{ Paths, StandardCopyOption, StandardOpenOption }

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.util.{ Failure, Success }

import com.fasterxml.jackson.databind.JsonNode

import controllers.core.BaseController
import model.exercisereading.ExerciseReader
import play.Logger
import play.data.DynamicForm

object XmlExerciseReader extends ExerciseReader[XmlExercise]("xml", XmlExercise.finder, classOf[Array[XmlExercise]]) {

  def checkOrCreateSampleFile(exercise: XmlExercise) =
    (!baseTargetDir.toFile.exists && !CommonUtils$.MODULE$.createDirectory(baseTargetDir)) match {
      case true ⇒ Logger.debug("Directory für Lösungsdateien (XML) " + baseTargetDir + "existiert nicht!")
      case false ⇒
        val filename = exercise.rootNode + "." + exercise.getReferenceFileEnding
        val providedFile = Paths.get("conf", "resources", exerciseType, filename).toAbsolutePath
        val targetPath = Paths.get(baseTargetDir.toString, filename).toAbsolutePath

        providedFile.toFile.exists match {
          case false ⇒
            Logger.debug("Konnte Datei nicht erstellen: Keine Lösungsdatei mitgeliefert...")
          case true ⇒ CommonUtils.copyFile(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING) match {
            case Success(path) ⇒ Logger.debug(s"Die Lösungsdatei $targetPath wurde erstellt.")
            case Failure(e)    ⇒ Logger.error(s"Fehler bei Erstellen von Musterlösung $targetPath", e)
          }
        }
    }

  override def initRemainingExFromForm(exercise: XmlExercise, form: DynamicForm) {
    exercise.exerciseType = XmlExType.valueOf(form.get(StringConsts.EXERCISE_TYPE))
    exercise.rootNode = form.get(StringConsts.ROOT_NODE_NAME)

    val referenceFilePath = Paths.get(
      BaseController.getSampleDir(exerciseType).toString,
      exercise.rootNode + "." + exercise.getReferenceFileEnding)
    val referenceFileContent = form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE).toList.asJava

    CommonUtils.writeFile(referenceFilePath, referenceFileContent, StandardOpenOption.TRUNCATE_EXISTING) match {
      case Success(_) ⇒ Unit
      case Failure(e) ⇒ Logger.error(s"Es gab einen Fehler beim Erstellen der Referenzdatei $referenceFilePath", e)
    }
  }

  override def instantiate(id: Int) = new XmlExercise(id)

  override def save(exercise: XmlExercise) {
    exercise.save
    checkOrCreateSampleFile(exercise)
  }

  override def updateExercise(exercise: XmlExercise, exerciseNode: JsonNode) {
    exercise.exerciseType = XmlExType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText)
    exercise.rootNode = exerciseNode.get(StringConsts.ROOT_NODE_NAME).asText
  }

}
