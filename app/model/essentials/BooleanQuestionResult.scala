package model.essentials

import model.Enums.SuccessType
import model.core.EvaluationResult

case class BooleanQuestionResult(override val success: SuccessType, learnerSolution: String, question: CreationQuestion) extends EvaluationResult {

  val assignments: Seq[BoolAssignment] = question.solutions

  val getSolutions: Seq[BoolAssignment] = question.solutions

}

