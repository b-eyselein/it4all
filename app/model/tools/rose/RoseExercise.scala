package model.tools.rose

import model.{ExPart, ExParts, ExerciseContent, SampleSolution}

abstract sealed class RoseExPart(val partName: String, val id: String) extends ExPart

object RoseExPart extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart(partName = "Robotersimulation", id = "robot_sim")

}

final case class RoseExerciseContent(
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  sampleSolutions: Seq[SampleSolution[String]]
) extends ExerciseContent {

  override protected type S = String

  override def parts: Seq[ExPart] = RoseExPart.values

}
