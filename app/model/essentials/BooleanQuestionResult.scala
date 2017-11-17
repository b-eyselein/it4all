package model.essentials

import model.Enums.SuccessType
import model.core.EvaluationResult

class BooleanQuestionResult(success: SuccessType, val learnerSolution: String, val question: CreationQuestion)
  extends EvaluationResult(success) {

  val assignments: List[BoolAssignment] = question.solutions

  def getSolutions: List[BoolAssignment] = question.solutions

}

