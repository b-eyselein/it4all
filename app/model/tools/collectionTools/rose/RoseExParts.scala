package model.tools.collectionTools.rose

import model.tools.collectionTools.{ExPart, ExParts}

abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart

object RoseExParts extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")

}
