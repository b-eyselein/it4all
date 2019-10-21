package model.tools.bool

import model.core.result.{EvaluationResult, SuccessType}
import model.tools.bool.BoolConsts._
import model.tools.bool.BoolTableRow.{disjunktiveNormalForm, konjunktiveNormalForm}
import play.api.libs.json._

sealed trait BooleanQuestionResult extends EvaluationResult {

  def toJson: JsValue

}

sealed trait CreationQuestionResult extends BooleanQuestionResult

final case class CreationQuestionError(formulaAsString: String, errorMsg: String) extends CreationQuestionResult {

  override def success: SuccessType = SuccessType.ERROR

  override def toJson: JsValue = Json.obj(
    successName -> success,
    formulaName -> formulaAsString,
    errorName -> errorMsg
  )

}

final case class CreationQuestionSuccess(learnerSolution: BoolNode, question: CreationQuestion) extends CreationQuestionResult {

  override def success: SuccessType = SuccessType.ofBool(question.solutions.forall(as => as(SolVariable) == learnerSolution(as)))

  val assignments: Seq[BoolTableRow] = question.solutions

  private def assignmentMapping(assignment: BoolTableRow): JsValue = Json.obj(
    idName -> assignment.identifier,
    "learnerVal" -> learnerSolution(assignment),
    correctName -> (assignment(SolVariable) == learnerSolution(assignment))
  )

  override def toJson: JsValue = Json.obj(
    successName -> success,
    assignmentsName -> JsArray(assignments.map(assignmentMapping)),
    knfName -> JsString(disjunktiveNormalForm(assignments).asString),
    dnfName -> JsString(konjunktiveNormalForm(assignments).asString)
  )
}


final case class FilloutQuestionResult(success: SuccessType, assignments: Seq[BoolTableRow], errorMsg: Option[String] = None)
  extends BooleanQuestionResult {

  override def toJson: JsValue = Json.obj(
    assignmentsName -> assignments.map { a =>
      Json.obj(
        idName -> a.identifier,
        "learner" -> JsBoolean(a.assignments.getOrElse(LerVariable, false)),
        "sample" -> JsBoolean(a.assignments.getOrElse(SolVariable, false))
      )
    }
  )

}
