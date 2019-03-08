package model

package object points {

  implicit def toPointsOps(p: Int): PointsOps = PointsOps(p)

  def addUp(points: Seq[Points]): Points = Points(points.map(_.quarters).sum)


  def zeroPoints: Points = zeroPoints

  def singlePoint: Points = 1 point

  def singleHalfPoint: Points = 1 halfPoint

  def singleQuarterPoint: Points = 1 quarterPoint

}
