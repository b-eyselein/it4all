package model.tools.rose

import model.tools._
import model.tools.programming.ProgDataType

abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart

object RoseExPart extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")

}

final case class RoseExerciseContent(
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  inputTypes: Seq[RoseInputType]
)

final case class RoseInputType(id: Int, name: String, inputType: ProgDataType)
