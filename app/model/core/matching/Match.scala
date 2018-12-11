package model.core.matching

import model._
import model.core.CoreConsts._
import model.core.JsonWriteable
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.language.postfixOps

trait AnalysisResult {

  val matchType: MatchType

  def toJson: JsValue

}

final case class GenericAnalysisResult(matchType: MatchType) extends AnalysisResult {

  override def toJson: JsObject = Json.obj(successName -> matchType.entryName)

}

trait Match extends JsonWriteable {

  type T
  type AR <: AnalysisResult

  val userArg  : Option[T]
  val sampleArg: Option[T]

  protected val analysisResult: Option[AR] = (userArg, sampleArg) match {
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

  protected def explanations: Seq[String] = matchType match {
    case MatchType.ONLY_USER                                    => Seq("Angabe ist falsch!")
    case MatchType.ONLY_SAMPLE                                  => Seq("Angabe fehlt!")
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH => Seq(s"Fehler beim Abgleich. Erwartet wurde ${sampleArg map descArg getOrElse ""}")
    case MatchType.SUCCESSFUL_MATCH                             => Seq[String]()
    case _                                                      => Seq("FEHLER!")
  }

  protected def analyze(arg1: T, arg2: T): AR

  protected def descArg(arg: T): String = arg.toString

  protected def descArgForJson(arg: T): JsValue

  def points: Points = -1 point

  def maxPoints: Points = -1 point

  override def toJson: JsValue = Json.obj(
    matchTypeName -> matchType.entryName,
    analysisResultName -> analysisResult.map(_.toJson),
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    explanationsName -> explanations
  )

}

