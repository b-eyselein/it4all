package model.matching

abstract class ScalaMatch[T](val userArg: Option[T], val sampleArg: Option[T]) {

  val matchType = (userArg, sampleArg) match {
    case (None, None) => throw new IllegalArgumentException("At least one arg of a match must not be None!")
    case (None, Some(_)) => MatchType.ONLY_SAMPLE
    case (Some(_), None) => MatchType.ONLY_USER
    case (Some(_), Some(_)) => analyze(userArg.get, sampleArg.get)
  }

  def getBSClass() = if (matchType == MatchType.SUCCESSFUL_MATCH) "success" else "warning"

  def getExplanation() = matchType match {
    case MatchType.FAILURE => "Es ist ein Fehler aufgetreten."
    case MatchType.ONLY_USER => "Angegeben, aber nicht in der Musterlösung vorhanden!"
    case MatchType.ONLY_SAMPLE => "Nur in der Musterlösung, nicht in der Lernerlösung vorhanden!"
    case MatchType.UNSUCCESSFUL_MATCH => "Fehler beim Abgleich."
    case MatchType.SUCCESSFUL_MATCH => "Korrekt."
  }

  def isSuccessful = matchType == MatchType.SUCCESSFUL_MATCH

  def analyze(arg1: T, arg2: T): MatchType
}

class ScalaGenericMatch[T](userArg: Option[T], sampleArg: Option[T]) extends ScalaMatch[T](userArg, sampleArg) {

  override def analyze(arg1: T, arg2: T) = MatchType.SUCCESSFUL_MATCH

}