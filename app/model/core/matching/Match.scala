package model.core.matching

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.CoreConsts._
import play.api.libs.json.{JsValue, Json}

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

  def explanations: Seq[String] = matchType match {
    case FAILURE                              => Seq("Es ist ein Fehler aufgetreten.")
    case ONLY_USER                            => Seq("Angabe ist falsch!")
    case ONLY_SAMPLE                          => Seq("Angabe fehlt!")
    case (UNSUCCESSFUL_MATCH | PARTIAL_MATCH) => Seq(s"Fehler beim Abgleich. Erwartet wurde ${sampleArg map descArg getOrElse ""}")
    case SUCCESSFUL_MATCH                     => Seq.empty
    case _                                    => Seq("FEHLER!")
  }

  def analyze(arg1: T, arg2: T): MatchType = SUCCESSFUL_MATCH

  protected def descArg(arg: T): String = arg.toString

  protected def descArgForJson(arg: T): JsValue

  def toJson: JsValue = Json.obj(
    successName -> matchType.name,
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    explanationsName -> explanations
  )

}

