package model;

import model.result.SuccessType
import model.result.EvaluationResult

class BooleanQuestionResult(success: SuccessType, val learnerSolution: String, val question: CreationQuestion)
  extends EvaluationResult(success) {

  val assignments = question.solutions

  def getSolutions = if (question == null) List.empty else question.solutions

}

