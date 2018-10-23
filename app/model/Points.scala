package model

import scala.math.Integral.Implicits._


final case class Points(quarters: Int) extends AnyVal {

  def +(that: Points): Points = Points(this.quarters + that.quarters)

  def *(multiplier: Int): Points = Points(multiplier * this.quarters)

  def asDoubleString: String = quarters /% 4 match {
    case (quotient, 0) => quotient.toString
    case (quotient, 1) => s"$quotient.25"
    case (quotient, 2) => s"$quotient.50"
    case (quotient, 3) => s"$quotient.75"
    case _             => "FEHLER!"
  }

}

final case class PointsOps(value: Int) extends AnyVal {

  def point: Points = points

  def points: Points = Points(value * 4)


  def halfPoint: Points = halfPoints

  def halfPoints: Points = Points(value * 2)


  def quarterPoint: Points = quarterPoints

  def quarterPoints: Points = Points(value)

}