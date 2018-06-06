package model.xml

import model.core.JsonWriteable
import model.core.result.{CompleteResult, EvaluationResult}
import model.xml.XmlConsts._
import play.api.libs.json.{JsValue, Json}
import play.twirl.api.Html

trait XmlEvaluationResult extends EvaluationResult with JsonWriteable {

  def render: String

}

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult] {

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
    pointsName -> points,
    maxPointsName -> maxPoints,
    "results" -> results.map(_.toJson)
  )

}

