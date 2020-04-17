package model.tools.rose

import model.tools._
import model.tools.programming.ProgDataType

abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart

object RoseExPart extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")

}

final case class RoseExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  sampleSolutions: Seq[SampleSolution[String]],
  content: RoseExerciseContent
) extends Exercise[String, RoseExerciseContent]

final case class RoseExerciseContent(
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  inputTypes: Seq[RoseInputType]
)

final case class RoseInputType(id: Int, name: String, inputType: ProgDataType)
