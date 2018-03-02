package model.bool

import model.Enums.SuccessType
import model.bool.BoolAssignment.{disjunktiveNormalForm, konjunktiveNormalForm}
import model.bool.BoolConsts.{LerVariable, SolVariable}
import model.core.EvaluationResult
import play.api.libs.json._

import scala.language.postfixOps

sealed trait BooleanQuestionResult extends EvaluationResult {

  def isCorrect: Boolean

  val assignments: Seq[BoolAssignment]

  override val success: SuccessType = SuccessType.ofBool(isCorrect)

  def toJson: JsValue

}

case class CreationQuestionResult(learnerSolution: ScalaNode, question: CreationQuestion, withSol: Boolean) extends BooleanQuestionResult {

  override val isCorrect: Boolean = question.solutions forall (as => as(SolVariable) == learnerSolution(as))

  override val assignments: Seq[BoolAssignment] = question.solutions

  private def assignmentMapping(assignment: BoolAssignment): JsValue = Json.obj(
    "id" -> assignment.identifier,
    "learnerVal" -> learnerSolution(assignment),
    "correct" -> (assignment(SolVariable) == learnerSolution(assignment))
  )

  override def toJson: JsValue = if (withSol) {
    Json.obj(
      "assignments" -> JsArray(assignments map assignmentMapping),
      "knf" -> disjunktiveNormalForm(assignments).asString,
      "dnf" -> konjunktiveNormalForm(assignments).asString
    )
  } else {
    Json.obj("assignments" -> JsArray(assignments map assignmentMapping))
  }
}

case class FilloutQuestionResult(formula: ScalaNode, assignments: Seq[BoolAssignment]) extends BooleanQuestionResult {

  override val isCorrect: Boolean = assignments forall (as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

  val variables: Seq[Variable] = formula.usedVariables toSeq

  override def toJson: JsValue = JsArray(assignments map { a =>
    Json.obj(
      "id" -> a.identifier,
      "assignments" -> JsObject(a.assignments.map {
        case (variable, bool) => variable.asString -> JsBoolean(bool)
      }.toSeq)
    )
  })

}