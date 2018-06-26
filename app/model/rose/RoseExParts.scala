package model.rose

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object RoseExParts extends Enum[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")

}
