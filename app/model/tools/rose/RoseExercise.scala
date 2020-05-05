package model.tools.rose

import model.{ExPart, ExParts, ExerciseContent, SampleSolution}
import model.tools._
import model.tools.programming.ProgDataType

abstract sealed class RoseExPart(val partName: String, val id: String) extends ExPart

object RoseExPart extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart(partName = "Robotersimulation", id = "robot_sim")

}

final case class RoseExerciseContent(
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  inputTypes: Seq[RoseInputType],
  sampleSolutions: Seq[SampleSolution[String]]
) extends ExerciseContent[String] {

  override def parts: Seq[ExPart] = RoseExPart.values

}

final case class RoseInputType(id: Int, name: String, inputType: ProgDataType)
