package model.xml

import model.core.JsonWriteable
import model.core.result.{CompleteResult, EvaluationResult}
import model.xml.XmlConsts._
import play.api.libs.json.{JsValue, Json}

trait XmlEvaluationResult extends EvaluationResult with JsonWriteable {

  def render: String

}

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult] {

  def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> isSuccessful,
    pointsName -> points,
    maxPointsName -> maxPoints,
    "results" -> results.map(_.toJson)
  )

}

