package model.rose

import controllers.exes.idPartExes.{ExPart, ExParts}


abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart

case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")


object RoseExParts extends ExParts[RoseExPart] {

  val values = Seq(RoseSingleExPart)

}
