package model

import play.api.libs.json.{JsNumber, Writes}

package object points {

  implicit def toPointsOps(p: Int): PointsOps = PointsOps(p)

  def addUp(points: Seq[Points]): Points = Points(points.map(_.quarters).sum)


  def zeroPoints: Points = 0.points

  def singlePoint: Points = 1.point

  def singleHalfPoint: Points = 1.halfPoint

  def singleQuarterPoint: Points = 1.quarterPoint


  val pointsJsonWrites: Writes[Points] = p => JsNumber(p.asDouble)

}
