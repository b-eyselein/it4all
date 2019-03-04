package model.tools.bool

import model.tools.bool.BoolTableRow.{disjunktiveNormalForm, konjunktiveNormalForm}
import model.tools.bool.BoolConsts.{LerVariable, SolVariable, _}
import model.core.result.{EvaluationResult, SuccessType}
import play.api.libs.json._

import scala.language.postfixOps

sealed trait BooleanQuestionResult extends EvaluationResult {

  def isCorrect: Boolean

  val assignments: Seq[BoolTableRow]

  def toJson: JsValue = JsObject(
    Seq("isSuccessful" -> JsBoolean(success != SuccessType.ERROR)) ++ restJson
  )

  def restJson: Seq[(String, JsValue)]

}

sealed trait CreationQuestionResult extends BooleanQuestionResult

final case class CreationQuestionError(formula: String, errorMsg: String) extends CreationQuestionResult {

  override def success: SuccessType = SuccessType.ERROR

  override def isCorrect: Boolean = false

  override val assignments: Seq[BoolTableRow] = Seq[BoolTableRow]()

  override def restJson: Seq[(String, JsValue)] = Seq(
    formulaName -> JsString(formula),
    errorName -> JsString(errorMsg)
  )

}

final case class CreationQuestionSuccess(learnerSolution: BoolNode, question: CreationQuestion) extends CreationQuestionResult {

  override val isCorrect: Boolean = question.solutions forall (as => as(SolVariable) == learnerSolution(as))

  override def success: SuccessType = SuccessType.ofBool(isCorrect)

  override val assignments: Seq[BoolTableRow] = question.solutions

  private def assignmentMapping(assignment: BoolTableRow): JsValue = Json.obj(
    idName -> assignment.identifier,
    "learnerVal" -> learnerSolution(assignment),
    correctName -> (assignment(SolVariable) == learnerSolution(assignment))
  )

  override def restJson: Seq[(String, JsValue)] = Seq[(String, JsValue)](
    assignmentsName -> JsArray(assignments map assignmentMapping),
    "knf" -> JsString(disjunktiveNormalForm(assignments).asString),
    "dnf" -> JsString(konjunktiveNormalForm(assignments).asString)
  )
}

sealed trait FilloutQuestionResult extends BooleanQuestionResult

final case class FilloutQuestionError(formula: String, errorMsg: String) extends FilloutQuestionResult {

  override def success: SuccessType = SuccessType.ERROR

  override def isCorrect: Boolean = false

  override def restJson: Seq[(String, JsValue)] = ???

  override val assignments: Seq[BoolTableRow] = Seq[BoolTableRow]()
}

final case class FilloutQuestionSuccess(formula: BoolNode, assignments: Seq[BoolTableRow]) extends FilloutQuestionResult {

  override val isCorrect: Boolean = assignments forall (as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

  override def success: SuccessType = SuccessType.ofBool(isCorrect)

  val variables: Seq[Variable] = formula.usedVariables toSeq

  override def restJson: Seq[(String, JsValue)] = Seq(assignmentsName -> JsArray(assignments map { a =>
    Json.obj(
      idName -> a.identifier,
      "learner" -> JsBoolean(a.assignments.getOrElse(LerVariable, false)),
      "sample" -> JsBoolean(a.assignments.getOrElse(SolVariable, false))
    )
  }))

}
