package model.core.matching

import model.points._

trait AnalysisResult {

  val matchType: MatchType

}

final case class GenericAnalysisResult(
  matchType: MatchType
) extends AnalysisResult

trait Match[T, AR <: AnalysisResult] {

  val userArg  : Option[T]
  val sampleArg: Option[T]

  val maybeAnalysisResult: Option[AR] //= (userArg zip sampleArg).map { case (ua, sa) => analyze(ua, sa) }

  def matchType: MatchType = maybeAnalysisResult match {
    case Some(ar) => ar.matchType
    case None     => userArg match {
      case None    => MatchType.ONLY_SAMPLE
      case Some(_) => MatchType.ONLY_USER
    }
  }

  def points: Points = (-1).point

  def maxPoints: Points = (-1).point

}

