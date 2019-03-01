package model

package object points {

  implicit def toPointsOps(p: Int): PointsOps = PointsOps(p)

  def addUp(points: Seq[Points]): Points = Points(points.map(_.quarters).sum)

}
