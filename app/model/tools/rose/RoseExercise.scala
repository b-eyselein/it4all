package model.tools.rose

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._
import model.tools.programming.ProgDataType

abstract sealed class RoseExPart(val partName: String, val urlName: String) extends ExPart

object RoseExPart extends ExParts[RoseExPart] {

  val values: IndexedSeq[RoseExPart] = findValues

  case object RoseSingleExPart extends RoseExPart("Robotersimulation", "robot_sim")

}

sealed trait RoseExTag extends EnumEntry

case object RoseExTag extends PlayEnum[RoseExTag] {

  override val values: IndexedSeq[RoseExTag] = findValues

  case object RoseExTagTodo extends RoseExTag

}

final case class RoseExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String,
  authors: Seq[String],
  text: String,
  tags: Seq[RoseExTag],
  difficulty: Option[Int],
  sampleSolutions: Seq[SampleSolution[String]],
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  inputTypes: Seq[RoseInputType]
) extends Exercise {

  override type ET      = RoseExTag
  override type SolType = String

}

final case class RoseExerciseContent(
  fieldWidth: Int,
  fieldHeight: Int,
  isMultiplayer: Boolean,
  inputTypes: Seq[RoseInputType],
  sampleSolutions: Seq[SampleSolution[String]]
) extends ExerciseContent {

  override type SolType = String

  def declaration(forUser: Boolean): String = {
    val className                = if (forUser) "UserRobot" else "SampleRobot"
    val (methodName, returnType) = if (isMultiplayer) ("act", "Action") else ("run", "None")

    val parameters = inputTypes match {
      case Seq() => ""
      case other => ", " + (other.map(it => it.name + ": " + it.inputType.typeName) mkString ", ")
    }

    s"""from base.robot import Robot
       |
       |class $className(Robot):
       |    def $methodName(self$parameters) -> $returnType:
       |        pass""".stripMargin
  }

}

final case class RoseInputType(id: Int, name: String, inputType: ProgDataType)