package model.exercisereading

import java.nio.file.Paths
import java.util.Optional

import scala.collection.JavaConverters.{ asScalaBufferConverter, asScalaIteratorConverter, seqAsJavaListConverter }

import com.fasterxml.jackson.databind.JsonNode

import io.ebean.Finder
import model.{ JsonReadable, StringConsts }
import model.StringConsts.{ AUTHOR_NAME, STATE_NAME, TEXT_NAME, TITLE_NAME }
import model.exercise.{ Exercise, ExerciseState }
import play.data.DynamicForm

abstract class ExerciseReader[E <: Exercise](e: String, f: Finder[Integer, E], classFor: Class[_])
  extends JsonReader[E](e, f, classFor) {

  val baseTargetDir = Paths.get("/data", "samples", exerciseType)

  def getOrInstantiateExercise(id: Int) = Optional.ofNullable(finder.byId(id)).orElse(instantiate(id))

  def initFromForm(id: Int, form: DynamicForm) = {
    val exercise = getOrInstantiateExercise(id)

    exercise.title = form.get(StringConsts.TITLE_NAME)
    exercise.author = form.get(StringConsts.AUTHOR_NAME)
    exercise.text = form.get(StringConsts.TEXT_NAME)

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

  def updateExercise(exercise: E, exerciseNode: JsonNode)

}

object ExerciseReader {
  
  def findMinimalNotUsedId[T <: JsonReadable](finder: Finder[Integer, T]): Int = {
    // FIXME: this is probably a ugly hack...
    val exercises = finder.all.asScala.sortWith(_.getId < _.getId)

    if (exercises.isEmpty)
      return 1
    else {
      exercises.sliding(2).foreach { exes ⇒
        if (exes(0).getId < exes(1).getId - 1)
          return exes(0).getId + 1
      }

      return exercises.last.getId() + 1
    }
  }

  def readArray[V](arrayNode: JsonNode, mappingFunction: JsonNode ⇒ V) =
    arrayNode.iterator.asScala.map(mappingFunction).toList.asJava
}
