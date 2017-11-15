package model.core.matching

import model.Enums.MatchType
import model.Enums.MatchType._
import play.twirl.api.Html


class Match[T](val userArg: Option[T], val sampleArg: Option[T], val size: Int) {

  val matchType: MatchType = (userArg, sampleArg) match {
    case (None, None)       => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_))    => ONLY_SAMPLE
    case (Some(_), None)    => ONLY_USER
    case (Some(_), Some(_)) => analyze(userArg.get, sampleArg.get)
  }

  def getBSClass: String = if (matchType == SUCCESSFUL_MATCH) "success" else "warning"

  def explanation = List(
    matchType match {
      case FAILURE            => "Es ist ein Fehler aufgetreten."
      case ONLY_USER          => "Angabe ist falsch!"
      case ONLY_SAMPLE        => "Angabe fehlt!"
      case UNSUCCESSFUL_MATCH => "Fehler beim Abgleich."
      case SUCCESSFUL_MATCH   => "Korrekt."
      case _                  => "FEHLER!"
    })

  lazy val isSuccessful: Boolean = matchType == SUCCESSFUL_MATCH

  def analyze(arg1: T, arg2: T): MatchType = SUCCESSFUL_MATCH

  def describeArg(arg: T): Html = new Html(
    s"""<td>
       |  <span class="text-${if (isSuccessful) "success" else "danger"}>${arg.toString}</span>
       |</td>""".stripMargin)

  private def describeArgOption(arg: Option[T]) = arg match {
    case Some(a) => describeArg(a)
    case None    => new Html("<td>&nbsp;</td>" * size)
  }

  def describeUserArg: Html = describeArgOption(userArg)

  def describeSampleArg: Html = describeArgOption(sampleArg)

}

