package model.matching

import play.twirl.api.Html
import scala.xml.Elem

class Match[T](val userArg: Option[T], val sampleArg: Option[T], val size: Int) {

  lazy val matchType = (userArg, sampleArg) match {
    case (None, None) => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_)) => MatchType.ONLY_SAMPLE
    case (Some(_), None) => MatchType.ONLY_USER
    case (Some(_), Some(_)) => analyze(userArg.get, sampleArg.get)
  }

  def getBSClass() = if (matchType == MatchType.SUCCESSFUL_MATCH) "success" else "warning"

  def explanation = List(matchType match {
    case MatchType.FAILURE => "Es ist ein Fehler aufgetreten."
    case MatchType.ONLY_USER => "Angabe ist falsch!"
    case MatchType.ONLY_SAMPLE => "Angabe fehlt!"
    case MatchType.UNSUCCESSFUL_MATCH => "Fehler beim Abgleich."
    case MatchType.SUCCESSFUL_MATCH => "Korrekt."
  })

  lazy val isSuccessful = matchType == MatchType.SUCCESSFUL_MATCH

  def analyze(arg1: T, arg2: T): MatchType = MatchType.SUCCESSFUL_MATCH

  def describeArg(arg: T): Html = new Html(s"""
<td>
  <span class="text-${if (isSuccessful) "success" else "danger"}>${arg.toString}</span>
</td>""")

  private def describeArgOption(arg: Option[T]) = arg match {
    case Some(arg) => describeArg(arg)
    case None => new Html("<td/>" * size)
  }

  def describeUserArg = describeArgOption(userArg)

  def describeSampleArg = describeArgOption(sampleArg)

}

