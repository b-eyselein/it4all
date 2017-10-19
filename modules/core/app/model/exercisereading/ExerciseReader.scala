package model.exercisereading

import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.util.Optional

import com.fasterxml.jackson.databind.JsonNode
import io.ebean.Finder
import model.JsonReadable
import model.StringConsts.{AUTHOR_NAME, STATE_NAME, TEXT_NAME, TITLE_NAME}
import model.exercise.{Exercise, ExerciseState}
import model.tools.ExToolObject
import play.data.DynamicForm

import scala.collection.JavaConverters.{asScalaBufferConverter, asScalaIteratorConverter, seqAsJavaListConverter}
import scala.util.Try

abstract class ExerciseReader[E <: Exercise](e: String, f: Finder[Integer, E], classFor: Class[_])
  extends JsonReader[E](e, f, classFor) {

  def getOrInstantiateExercise(id: Int): E = Optional.ofNullable(finder.byId(id)).orElse(instantiate(id))

  def initFromForm(id: Int, form: DynamicForm): SingleReadingResult[E] = {
    val exercise = getOrInstantiateExercise(id)

    exercise.title = form.get(TITLE_NAME)
    exercise.author = form.get(AUTHOR_NAME)
    exercise.text = form.get(TEXT_NAME)

    initRemainingExFromForm(exercise, form)

    SingleReadingResult(exercise)
  }

  def initRemainingExFromForm(exercise: E, form: DynamicForm)

  def update(exercise: E, node: JsonNode) {
    exercise.title = node.get(TITLE_NAME).asText()
    exercise.author = node.get(AUTHOR_NAME).asText()
    exercise.text = JsonReader.readAndJoinTextArray(node.get(TEXT_NAME))

    exercise.state = Option(node.get(STATE_NAME)) match {
      case Some(stateNode) => ExerciseState.valueOf(stateNode.asText)
      case None => ExerciseState.CREATED
    }

    updateExercise(exercise, node)
  }

  protected def checkOrCreateSampleFile(exercise: Exercise, toolObject: ExToolObject, filename: String): Try[Path] = {
    val providedFile = Paths.get(resourcesFolder.toString, filename).toAbsolutePath
    val targetPath = Paths.get(toolObject.sampleDir.toString, filename).toAbsolutePath

    Try(Files.createDirectories(toolObject.sampleDir))
      .map(_ => Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING))
  }

  def updateExercise(exercise: E, exerciseNode: JsonNode)


}

object ExerciseReader {

  def findMinimalNotUsedId[T <: JsonReadable](finder: Finder[Integer, T]): Int = {
    // FIXME: this is probably a ugly hack...
    val exercises = finder.all.asScala.sortWith(_.getId < _.getId)

    if (exercises.isEmpty) 1
    else {
      exercises.sliding(2).foreach { exes =>
        if (exes.head.getId < exes(1).getId - 1)
          return exes.head.getId + 1
      }

      exercises.last.getId + 1
    }
  }

  def readArray[V](arrayNode: JsonNode, mappingFunction: JsonNode => V): List[V] =
    arrayNode.iterator.asScala.map(mappingFunction).toList
}
