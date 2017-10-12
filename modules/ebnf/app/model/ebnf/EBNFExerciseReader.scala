package model.ebnf

import com.fasterxml.jackson.databind.JsonNode

import model.exercisereading.{ExerciseReader, JsonReader}
import play.data.DynamicForm

object EBNFExerciseReader extends ExerciseReader[EBNFExercise]("ebnf", EBNFExercise.finder, classOf[Array[EBNFExercise]]) {

  override def initRemainingExFromForm(exercise: EBNFExercise, form: DynamicForm): Unit = {
    exercise.terminals = Option(form.get("terminals")) match {
      case None => ""
      case Some(str) => str
    }
  }

  override def instantiate(id: Int) = new EBNFExercise(id)

  override def updateExercise(exercise: EBNFExercise, jsonNode: JsonNode): Unit = {
    exercise.terminals = JsonReader.readAndJoinTextArray(jsonNode.get("terminals"), ",")
  }

}