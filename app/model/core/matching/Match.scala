package model.core.matching

import model.core.CoreConsts._
import model.points._
import play.api.libs.json._

trait AnalysisResult {

  val matchType: MatchType

  def toJson: JsValue

}

final case class GenericAnalysisResult(matchType: MatchType) extends AnalysisResult {

  override def toJson: JsObject = Json.obj(successName -> matchType.entryName)

}

trait Match {

  type T
  type AR <: AnalysisResult

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val analysisResult: Option[AR] = (userArg zip sampleArg).headOption.map { case (ua, sa) => analyze(ua, sa) }

  val matchType: MatchType = analysisResult match {
    case Some(ar) => ar.matchType
    case None     => userArg match {
      case None    => MatchType.ONLY_SAMPLE
      case Some(_) => MatchType.ONLY_USER
    }
  }

  protected def analyze(arg1: T, arg2: T): AR

  protected def descArgForJson(arg: T): JsValue

  def points: Points = -1 point

  def maxPoints: Points = -1 point

  def toJson: JsValue = Json.obj(
    matchTypeName -> matchType.entryName,
    analysisResultName -> analysisResult.map(_.toJson),
    userArgName -> userArg.map(descArgForJson),
    sampleArgName -> sampleArg.map(descArgForJson),
  )

}

