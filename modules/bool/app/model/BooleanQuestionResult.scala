package model;

import model.exercise.Success
import model.result.EvaluationResult

class BooleanQuestionResult(success: Success, val learnerSolution: String, val question: CreationQuestion)
  extends EvaluationResult(success) {

  val assignments = question.solutions

  def getSolutions = if (question == null) List.empty else question.solutions

}

