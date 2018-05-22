package model.xml

import model.core.JsonWriteable
import model.core.result.{CompleteResult, EvaluationResult}
import model.xml.XmlConsts._
import play.api.libs.json.{JsValue, Json}
import play.twirl.api.{Html, HtmlFormat}

trait XmlEvaluationResult extends EvaluationResult with JsonWriteable {

  def render: String

}

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult] {

  override type SolType = String

  val learnerSolution: String
  val solutionSaved  : Boolean
  val results        : Seq[XmlEvaluationResult]

  override def renderLearnerSolution: Html = new Html("<pre>" + HtmlFormat.escape(learnerSolution).toString + "</pre>")

  def render: Html = {
    val solSaved: String = if (solutionSaved)
      s"""<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Ihre Lösung wurde gespeichert.</div>"""
    else
      s"""<div class="alert alert-danger"><span class="glyphicon glyphicon-remove"></span> Ihre Lösung konnte nicht gespeichert werden!</div>"""

    val resultsRender: String = results match {
      case Nil => s"""<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Es wurden keine Fehler gefunden.</div>"""
      case res => res map (_.render) mkString "\n"
    }

    new Html(solSaved + resultsRender)
  }

  def toJson: JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> isSuccessful,
    "results" -> results.map(_.toJson)
  )

}

