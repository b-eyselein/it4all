package model.ebnf

import com.fasterxml.jackson.databind.JsonNode

import model.exercisereading.{ ExerciseReader, JsonReader }
import play.data.DynamicForm

object EBNFExerciseReader extends ExerciseReader[EBNFExercise]("ebnf", EBNFExercise.finder, classOf[Array[EBNFExercise]]) {

  override def initRemainingExFromForm(exercise: EBNFExercise, form: DynamicForm) = {
    exercise.terminals = form.get("terminals") match {
      case null ⇒ ""
      case s    ⇒ s
    }
  }

  override def instantiate(id: Int) = new EBNFExercise(id)

  override def updateExercise(exercise: EBNFExercise, jsonNode: JsonNode) = {
    exercise.terminals = JsonReader.readAndJoinTextArray(jsonNode.get("terminals"), ",")
  }

}