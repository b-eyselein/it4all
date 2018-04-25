package model.core.result

import play.twirl.api.Html


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

  def getBSClass: String = success.color

  def getGlyphicon: String = success.glyphicon

  def getPoints: Int = success.points

  def isSuccessful: Boolean = success == SuccessType.COMPLETE

}

trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  type SolType

  def learnerSolution: SolType

  def solutionSaved: Boolean

  def results: Seq[E]

  def renderLearnerSolution: Html

  override def success: SuccessType = SuccessType.ofBool(EvaluationResult.allResultsSuccessful(results))

  protected def solSavedRender: String = {
    val (bsClass, glyphicon) = if (solutionSaved) ("success", "ok") else ("danger", "remove")

    val text = if (solutionSaved) "Ihre Lösung wurde gespeichert." else "Ihre Lösung konnte nicht gespeichert werden!"

    s"""<div class="alert alert-$bsClass"><span class="glyphicon glyphicon-$glyphicon"></span> $text</div>"""
  }

}
