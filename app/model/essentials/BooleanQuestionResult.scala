package model.essentials

import model.Enums.SuccessType
import model.core.EvaluationResult
import model.essentials.EssentialsConsts.{LerVariable, SolVariable}

import scala.language.postfixOps

sealed trait BooleanQuestionResult extends EvaluationResult {

  val assignments: Seq[BoolAssignment]

}

case class CreationQuestionResult(override val success: SuccessType, learnerSolution: String, question: CreationQuestion) extends BooleanQuestionResult {

  override val assignments: Seq[BoolAssignment] = question.solutions

  val getSolutions: Seq[BoolAssignment] = question.solutions

}

case class FilloutQuestionResult(formula: ScalaNode, assignments: Seq[BoolAssignment]) extends BooleanQuestionResult {

  val isCorrect: Boolean = assignments forall (as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

  override val success: SuccessType = SuccessType.ofBool(isCorrect)

  val variables: Seq[Variable] = formula.usedVariables toSeq


}