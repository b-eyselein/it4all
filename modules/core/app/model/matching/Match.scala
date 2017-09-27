package model.matching

import play.twirl.api.Html
import scala.xml.Elem

abstract class Match[T](val userArg: Option[T], val sampleArg: Option[T]) {

  val matchType = (userArg, sampleArg) match {
    case (None, None) => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_)) => MatchType.ONLY_SAMPLE
    case (Some(_), None) => MatchType.ONLY_USER
    case (Some(_), Some(_)) => analyze(userArg.get, sampleArg.get)
  }

  def getBSClass() = if (matchType == MatchType.SUCCESSFUL_MATCH) "success" else "warning"

  def explanation = matchType match {
    case MatchType.FAILURE => "Es ist ein Fehler aufgetreten."
    case MatchType.ONLY_USER => "Angegeben, aber nicht in der Musterl\u00F6sung vorhanden!"
    case MatchType.ONLY_SAMPLE => "Nur in der Musterlösung, nicht in der Lernerl\u00F6sung vorhanden!"
    case MatchType.UNSUCCESSFUL_MATCH => "Fehler beim Abgleich."
    case MatchType.SUCCESSFUL_MATCH => "Korrekt."
  }

  lazy val isSuccessful = matchType == MatchType.SUCCESSFUL_MATCH

  def analyze(arg1: T, arg2: T): MatchType

  def describeArg(arg: Option[T]): Html = if(arg.isDefined) 
    new Html(s"""
<td>
  <span class="text-${if(isSuccessful) "success" else "danger"}>
  ${arg.get.toString}</span>
</td>""")
  else new Html("<td/>")

  def describeUserArg = describeArg(userArg)

  def describeSampleArg = describeArg(sampleArg)

}

class ScalaGenericMatch[T](userArg: Option[T], sampleArg: Option[T]) extends Match[T](userArg, sampleArg) {

  override def analyze(arg1: T, arg2: T) = MatchType.SUCCESSFUL_MATCH

}