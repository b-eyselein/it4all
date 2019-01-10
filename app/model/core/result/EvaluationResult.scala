package model.core.result

import model._
import play.api.libs.json.JsValue

import scala.language.postfixOps


object EvaluationResult {

  implicit class PimpedHtmlString(string: String) {

    def asCode: String = "<code>" + string + "</code>"

    def asListElem: String = s"<li>$string</li>"

    def asDiv: String = s"<div>$string</div>"

    def asDivWithClass(clazz: String): String = s"""<div class="$clazz">$string</div>"""

  }

  def notAllResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.exists(!_.isSuccessful)

  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.nonEmpty && results.forall(_.isSuccessful)

  def asMsg(successType: SuccessType, msg: String): String = s"""<p><span class="${successType.glyphicon}></span> $msg"""

  def asMsg(success: Boolean, msg: String): String = s"""<p><span class="glyphicon glyphicon-${if (success) "ok" else "remove"}"></span> $msg</p>"""

}

trait EvaluationResult {

  def success: SuccessType

  def isSuccessful: Boolean = success == SuccessType.COMPLETE

}

trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  type SolType

  def points: Points = -1 point

  def maxPoints: Points = -1 point

  def learnerSolution: SolType

  //  def solutionSaved: Boolean

  def results: Seq[E]

  def toJson(saved: Boolean): JsValue

  override def success: SuccessType = SuccessType.ofBool(EvaluationResult.allResultsSuccessful(results))

}
