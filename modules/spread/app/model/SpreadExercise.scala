package model

import javax.persistence.Entity

import com.fasterxml.jackson.databind.JsonNode
import io.ebean.Finder
import model.exercise.Exercise
import model.exercisereading.ExerciseReader
import play.data.DynamicForm

@Entity
class SpreadExercise(i: Int) extends Exercise(i) {
  var sampleFilename: String = _

  var templateFilename: String = _
}

object SpreadExercise {
  val finder: Finder[Integer, SpreadExercise] = new Finder(classOf[SpreadExercise])
}

object SpreadExerciseReader
  extends ExerciseReader[SpreadExercise]("spread", SpreadExercise.finder, classOf[Array[SpreadExercise]]) {

  override def initRemainingExFromForm(exercise: SpreadExercise, form: DynamicForm) {
    exercise.templateFilename = form.get(StringConsts.TEMPALTE_FILENAME)
    exercise.sampleFilename = form.get(StringConsts.SAMPLE_FILENAME)
  }

  override def instantiate(id: Int) = new SpreadExercise(id)

  override def save(exercise: SpreadExercise) {
    exercise.save()
    // FIXME: implement with toolObject
    //    List("xlsx", "ods").map(exercise.sampleFilename + "." + _).foreach(fileName => {
    //      val tr = checkOrCreateSampleFile(exercise, toolObject, fileName) match {
    //        case Failure(e) => Logger.error(s"An error has occured while saving the sample file $fileName", e)
    //        case Success(s) => Logger.info(s"Solution file $fileName was created successfully")
    //      }
    //    })
  }

  override def updateExercise(exercise: SpreadExercise, exerciseNode: JsonNode) {
    exercise.sampleFilename = exerciseNode.get(StringConsts.SAMPLE_FILENAME).asText
    exercise.templateFilename = exerciseNode.get(StringConsts.TEMPALTE_FILENAME).asText
  }

}
