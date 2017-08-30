package model.ebnf

import model.exercisereading.ExerciseReader
import play.data.DynamicForm
import com.fasterxml.jackson.databind.JsonNode

class EBNFExerciseReader extends ExerciseReader[EBNFExercise]("ebnf", EBNFExercise.finder, classOf[Array[EBNFExercise]]) {

  override def initRemainingExFromForm(exercise: EBNFExercise, form: DynamicForm) = exercise.terminals = form.get("terminals")

  override def instantiateExercise(id: Int) = new EBNFExercise(id)

  override def saveRead(exercise: EBNFExercise) = exercise.save()

  override def updateExercise(exercise: EBNFExercise, jsonNode: JsonNode) = exercise.terminals = jsonNode.get("terminals").asText()

}