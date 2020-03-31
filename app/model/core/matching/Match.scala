package model.core.matching

import model.points._

trait Match[T] {

  val matchType: MatchType
  val userArg: Option[T]
  val sampleArg: Option[T]

  def points: Points = (-1).point

  def maxPoints: Points = (-1).point

  def userArgDescription: Option[String]   = userArg.map(_.toString)
  def sampleArgDescription: Option[String] = sampleArg.map(_.toString)

}
