package model.core.matching

import model.points._

trait Match[T] {

  val matchType: MatchType
  val userArg: Option[T]
  val sampleArg: Option[T]

  def points: Points = (-1).point

  def maxPoints: Points = (-1).point

  protected def argDescription: T => String = (t: T) => t.toString

  def userArgDescription: Option[String]   = userArg.map(argDescription)
  def sampleArgDescription: Option[String] = sampleArg.map(argDescription)

}
