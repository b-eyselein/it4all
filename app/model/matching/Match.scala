package model.matching

import model.points._

trait Match[T] {

  val matchType: MatchType

  val userArg: T
  val sampleArg: T

  def points: Points    = (-1).point
  def maxPoints: Points = (-1).point

  protected def argDescription: T => String = (t: T) => t.toString

  def userArgDescription: String   = argDescription(userArg)
  def sampleArgDescription: String = argDescription(sampleArg)

}
