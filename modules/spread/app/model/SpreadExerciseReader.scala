package model

import scala.util.{Failure, Success}

import com.fasterxml.jackson.databind.JsonNode

import model.exercisereading.ExerciseReader
import play.Logger
import play.data.DynamicForm

object SpreadExerciseReader
  extends ExerciseReader[SpreadExercise]("spread", SpreadExercise.finder, classOf[Array[SpreadExercise]]) {

  override def initRemainingExFromForm(exercise: SpreadExercise, form: DynamicForm) {
    exercise.templateFilename = form.get(StringConsts.TEMPALTE_FILENAME)
    exercise.sampleFilename = form.get(StringConsts.SAMPLE_FILENAME)
  }

  override def instantiate(id: Int) = new SpreadExercise(id)

  override def save(exercise: SpreadExercise) {
    exercise.save
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
