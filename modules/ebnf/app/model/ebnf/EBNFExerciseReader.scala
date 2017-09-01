package model.ebnf

import com.fasterxml.jackson.databind.JsonNode

import model.exercisereading.ExerciseReader
import play.data.DynamicForm

class EBNFExerciseReader extends ExerciseReader[EBNFExercise]("ebnf", EBNFExercise.finder, classOf[Array[EBNFExercise]]) {

  override def initRemainingExFromForm(exercise: EBNFExercise, form: DynamicForm) = {
    exercise.terminals = form.get("terminals") match {
      case null => ""
      case s => s
    }
  }

  override def instantiateExercise(id: Int) = new EBNFExercise(id)

  override def saveExercise(exercise: EBNFExercise) = exercise.save()

  override def updateExercise(exercise: EBNFExercise, jsonNode: JsonNode) = exercise.terminals = jsonNode.get("terminals").asText()

}