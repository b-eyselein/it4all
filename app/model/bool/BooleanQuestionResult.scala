package model.bool

import model.bool.BoolAssignment.{disjunktiveNormalForm, konjunktiveNormalForm}
import model.bool.BoolConsts.{LerVariable, SolVariable, _}
import model.core.result.{EvaluationResult, SuccessType}
import play.api.libs.json._

import scala.language.postfixOps

sealed trait BooleanQuestionResult extends EvaluationResult {

  def isCorrect: Boolean

  val assignments: Seq[BoolAssignment]

  override val success: SuccessType = SuccessType.ofBool(isCorrect)

  def toJson: JsValue

}

case class CreationQuestionResult(learnerSolution: ScalaNode, question: CreationQuestion) extends BooleanQuestionResult {

  override val isCorrect: Boolean = question.solutions forall (as => as(SolVariable) == learnerSolution(as))

  override val assignments: Seq[BoolAssignment] = question.solutions

  private def assignmentMapping(assignment: BoolAssignment): JsValue = Json.obj(
    idName -> assignment.identifier,
    "learnerVal" -> learnerSolution(assignment),
    correctName -> (assignment(SolVariable) == learnerSolution(assignment))
  )

  override def toJson: JsValue = Json.obj(
    assignmentsName -> JsArray(assignments map assignmentMapping),
    "knf" -> disjunktiveNormalForm(assignments).asString,
    "dnf" -> konjunktiveNormalForm(assignments).asString
  )
}

case class FilloutQuestionResult(formula: ScalaNode, assignments: Seq[BoolAssignment]) extends BooleanQuestionResult {

  override val isCorrect: Boolean = assignments forall (as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

  val variables: Seq[Variable] = formula.usedVariables toSeq

  override def toJson: JsValue = JsArray(assignments map { a =>
    Json.obj(
      idName -> a.identifier,
      "learner" -> JsBoolean(a.assignments.getOrElse(LerVariable, false)),
      "sample" -> JsBoolean(a.assignments.getOrElse(SolVariable, false))
    )
  })

}