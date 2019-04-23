package model.core.matching

import model.core.CoreConsts._
import model.points._
import play.api.libs.json._

import scala.language.postfixOps

trait AnalysisResult {

  val matchType: MatchType

  def toJson: JsValue

}

final case class GenericAnalysisResult(matchType: MatchType) extends AnalysisResult {

  override def toJson: JsObject = Json.obj(successName -> matchType.entryName)

}

//object MatchJsonProtocol {
//
//  private def unapplyMatch: Match => (MatchType, Option[JsValue], Option[JsValue], Option[JsValue]) =
//    m => (m.matchType, m.analysisResult.map(_.toJson), m.userArg.map(m.descArgForJson(_)), m.sampleArg.map(m.descArgForJson(_)))
//
//  val matchJsonWrites: Writes[Match] = (
//    (__ \ matchTypeName).write[MatchType] and
//      (__ \ analysisResultName).write[Option[JsValue]] and
//      (__ \ userArgName).write[Option[JsValue]] and
//      (__ \ sampleArgName).write[Option[JsValue]]
//    ) (unapplyMatch)
//
//}

trait Match {

  type T
  type AR <: AnalysisResult

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val analysisResult: Option[AR] = (userArg, sampleArg) match {
    // FIXME: refactor!
    case (Some(ua), Some(sa)) => Some(analyze(ua, sa))
    case _                    => None
  }

  val matchType: MatchType = analysisResult match {
    case Some(ar) => ar.matchType
    case None     => userArg match {
      case None    => MatchType.ONLY_SAMPLE
      case Some(_) => MatchType.ONLY_USER
    }
  }

  protected def analyze(arg1: T, arg2: T): AR

  protected def descArg(arg: T): String = arg.toString

  protected def descArgForJson(arg: T): JsValue

  def points: Points = -1 point

  def maxPoints: Points = -1 point

  def toJson: JsValue = Json.obj(
    matchTypeName -> matchType.entryName,
    analysisResultName -> analysisResult.map(_.toJson),
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
  )

}

