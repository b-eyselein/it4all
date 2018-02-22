package model.core.matching

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.EvaluationResult.PimpedHtmlString
import play.api.libs.json.{JsString, JsValue, Json}

trait Match[T] {

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val matchType: MatchType = (userArg, sampleArg) match {
    case (None, None)         => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_))      => ONLY_SAMPLE
    case (Some(_), None)      => ONLY_USER
    case (Some(ua), Some(sa)) => analyze(ua, sa)
  }

  def getBSClass: String = matchType match {
    case SUCCESSFUL_MATCH => "success"
    case PARTIAL_MATCH    => "warning"
    case _                => "danger"
  }

  def explanation: String = matchType match {
    case FAILURE                              => "Es ist ein Fehler aufgetreten."
    case ONLY_USER                            => "Angabe ist falsch!"
    case ONLY_SAMPLE                          => "Angabe fehlt!"
    case (UNSUCCESSFUL_MATCH | PARTIAL_MATCH) => s"Fehler beim Abgleich. Erwartet wurde ${sampleArg map descArg getOrElse ""}"
    case SUCCESSFUL_MATCH                     => "Korrekt."
    case _                                    => "FEHLER!"
  }

  lazy val isSuccessful: Boolean = matchType == SUCCESSFUL_MATCH

  def analyze(arg1: T, arg2: T): MatchType = SUCCESSFUL_MATCH

  def descUserArgWithReason(implicit isCorrect: Boolean): String = userArg map (descArg(_).asCode + (if (isCorrect) "" else ": " + explanation)) getOrElse ""

  def descSampleArgWithReason(implicit isCorrect: Boolean): String = sampleArg map (descArg(_).asCode + ": " + explanation) getOrElse ""

  protected def descArg(arg: T): String = arg.toString

  def toJson: JsValue = matchType match {
    case ONLY_USER   => JsString(userArg map (_.toString) getOrElse "")
    case ONLY_SAMPLE => JsString(sampleArg map (_.toString) getOrElse "")
    case _           => Json.obj(
      "success" -> matchType.name,
      "userArg" -> JsString(userArg map (_.toString) getOrElse ""),
      "sampleArg" -> JsString(sampleArg map (_.toString) getOrElse "")
    )
  }

}

