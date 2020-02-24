package model

import scala.language.implicitConversions

package object points {

  final case class Points(quarters: Int) extends AnyVal with Ordered[Points] {

    def +(that: Points): Points = Points(this.quarters + that.quarters)

    def *(multiplier: Int): Points = Points(multiplier * this.quarters)

    def asDouble: Double = quarters / 4d

    override def compare(that: Points): Int = this.quarters - that.quarters

  }

  final case class PointsOps(value: Int) extends AnyVal {

    def point: Points = points

    def points: Points = Points(value * 4)

    def halfPoint: Points = halfPoints

    def halfPoints: Points = Points(value * 2)

    def quarterPoint: Points = quarterPoints

    def quarterPoints: Points = Points(value)

  }

  implicit def toPointsOps(p: Int): PointsOps = PointsOps(p)

  def addUp(points: Seq[Points]): Points = Points(points.map(_.quarters).sum)

  def zeroPoints: Points = 0.points

  def singlePoint: Points = 1.point

  def singleHalfPoint: Points = 1.halfPoint

  def singleQuarterPoint: Points = 1.quarterPoint

}
