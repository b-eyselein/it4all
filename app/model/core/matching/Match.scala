package model.core.matching

import model.core.CoreConsts._
import play.api.libs.json.{JsObject, JsValue, Json}

trait AnalysisResult {

  val matchType: MatchType

  def toJson: JsValue

}

case class GenericAnalysisResult(matchType: MatchType) extends AnalysisResult {

  override def toJson: JsObject = Json.obj(successName -> matchType.entryName)

}

trait Match[T] {

  type MatchAnalysisResult <: AnalysisResult

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val analysisResult: Option[MatchAnalysisResult] = (userArg, sampleArg) match {
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

  def explanations: Seq[String] = matchType match {
    case MatchType.FAILURE                                      => Seq("Es ist ein Fehler aufgetreten.")
    case MatchType.ONLY_USER                                    => Seq("Angabe ist falsch!")
    case MatchType.ONLY_SAMPLE                                  => Seq("Angabe fehlt!")
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH => Seq(s"Fehler beim Abgleich. Erwartet wurde ${sampleArg map descArg getOrElse ""}")
    case MatchType.SUCCESSFUL_MATCH                             => Seq.empty
    case _                                                      => Seq("FEHLER!")
  }

  protected def analyze(arg1: T, arg2: T): MatchAnalysisResult

  protected def descArg(arg: T): String = arg.toString

  protected def descArgForJson(arg: T): JsValue

  def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    analysisResultName -> analysisResult.map(_.toJson),
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    explanationsName -> explanations
  )

}

