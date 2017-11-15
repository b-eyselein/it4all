package model.bool

import model.Enums.SuccessType
import model.core.EvaluationResult

class BooleanQuestionResult(success: SuccessType, val learnerSolution: String, val question: CreationQuestion)
  extends EvaluationResult(success) {

  val assignments: List[Assignment] = question.solutions

  def getSolutions: List[Assignment] = question.solutions

}

