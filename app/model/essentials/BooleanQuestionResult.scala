package model.essentials

import model.Enums.SuccessType
import model.core.EvaluationResult
import model.essentials.EssentialsConsts.{LerVariable, SolVariable}

import scala.language.postfixOps

sealed trait BooleanQuestionResult extends EvaluationResult {

  def isCorrect: Boolean

  val assignments: Seq[BoolAssignment]

  override val success: SuccessType = SuccessType.ofBool(isCorrect)

}

case class CreationQuestionResult(learnerSolution: ScalaNode, question: CreationQuestion, withSol: Boolean) extends BooleanQuestionResult {

  override val isCorrect: Boolean = question.solutions forall (as => as(SolVariable) == learnerSolution(as))

  override val assignments: Seq[BoolAssignment] = question.solutions

}

case class FilloutQuestionResult(formula: ScalaNode, assignments: Seq[BoolAssignment]) extends BooleanQuestionResult {

  override val isCorrect: Boolean = assignments forall (as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

  val variables: Seq[Variable] = formula.usedVariables toSeq

}