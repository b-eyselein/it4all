package model.core.matching

import model.core.CoreConsts._
import play.api.libs.json.{JsValue, Json}

trait Match[T] {

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val matchType: MatchType = (userArg, sampleArg) match {
    case (None, None)         => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_))      => MatchType.ONLY_SAMPLE
    case (Some(_), None)      => MatchType.ONLY_USER
    case (Some(ua), Some(sa)) => analyze(ua, sa)
  }

  def getBSClass: String = matchType match {
    case MatchType.SUCCESSFUL_MATCH => "success"
    case MatchType.PARTIAL_MATCH    => "warning"
    case _                          => "danger"
  }

  def explanations: Seq[String] = matchType match {
    case MatchType.FAILURE                                      => Seq("Es ist ein Fehler aufgetreten.")
    case MatchType.ONLY_USER                                    => Seq("Angabe ist falsch!")
    case MatchType.ONLY_SAMPLE                                  => Seq("Angabe fehlt!")
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH => Seq(s"Fehler beim Abgleich. Erwartet wurde ${sampleArg map descArg getOrElse ""}")
    case MatchType.SUCCESSFUL_MATCH                             => Seq.empty
    case _                                                      => Seq("FEHLER!")
  }

  def analyze(arg1: T, arg2: T): MatchType = MatchType.SUCCESSFUL_MATCH

  protected def descArg(arg: T): String = arg.toString

  protected def descArgForJson(arg: T): JsValue

  def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    explanationsName -> explanations
  )

}

