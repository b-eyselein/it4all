package model

import model.result.{EvaluationResult, SuccessType}

class BooleanQuestionResult(success: SuccessType, val learnerSolution: String, val question: CreationQuestion)
  extends EvaluationResult(success) {

  val assignments: List[Assignment] = question.solutions

  def getSolutions: List[Assignment] = question.solutions

}

