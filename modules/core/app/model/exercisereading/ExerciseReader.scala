package model.exercisereading

import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.Optional

import scala.collection.JavaConverters.{asScalaBufferConverter, asScalaIteratorConverter, seqAsJavaListConverter}
import scala.util.Try

import com.fasterxml.jackson.databind.JsonNode

import io.ebean.Finder
import model.JsonReadable
import model.StringConsts.{AUTHOR_NAME, STATE_NAME, TEXT_NAME, TITLE_NAME}
import model.exercise.{Exercise, ExerciseState}
import play.data.DynamicForm

abstract class ExerciseReader[E <: Exercise](e: String, f: Finder[Integer, E], classFor: Class[_])
  extends JsonReader[E](e, f, classFor) {


  def getOrInstantiateExercise(id: Int) = Optional.ofNullable(finder.byId(id)).orElse(instantiate(id))

  def initFromForm(id: Int, form: DynamicForm) = {
    val exercise = getOrInstantiateExercise(id)

    exercise.title = form.get(TITLE_NAME)
    exercise.author = form.get(AUTHOR_NAME)
    exercise.text = form.get(TEXT_NAME)

    initRemainingExFromForm(exercise, form)

    exercise
  }

  def initRemainingExFromForm(exercise: E, form: DynamicForm)

  def update(exercise: E, node: JsonNode) {
    exercise.title = node.get(TITLE_NAME).asText()
    exercise.author = node.get(AUTHOR_NAME).asText()
    exercise.text = JsonReader.readAndJoinTextArray(node.get(TEXT_NAME), "")

    val stateNode = node.get(STATE_NAME)
    exercise.state = if (stateNode != null) ExerciseState.valueOf(stateNode.asText()) else ExerciseState.CREATED

    updateExercise(exercise, node)
  }

  def checkOrCreateSampleFile(exercise: Exercise, filename: String) = {
    val providedFile = Paths.get(resourcesFolder.toString, filename).toAbsolutePath
    val targetPath = Paths.get(baseTargetDir.toString, filename).toAbsolutePath

    Try(Files.createDirectories(baseTargetDir))
      .map(_ => Files.copy(providedFile, targetPath, StandardCopyOption.REPLACE_EXISTING))
  }

  def updateExercise(exercise: E, exerciseNode: JsonNode)


}

object ExerciseReader {

  def findMinimalNotUsedId[T <: JsonReadable](finder: Finder[Integer, T]): Int = {
    // FIXME: this is probably a ugly hack...
    val exercises = finder.all.asScala.sortWith(_.getId < _.getId)

    if (exercises.isEmpty)
      1
    else {
      exercises.sliding(2).foreach { exes =>
        if (exes(0).getId < exes(1).getId - 1)
          return exes(0).getId + 1
      }

      exercises.last.getId() + 1
    }
  }

  def readArray[V](arrayNode: JsonNode, mappingFunction: JsonNode => V) =
    arrayNode.iterator.asScala.map(mappingFunction).toList.asJava
}
