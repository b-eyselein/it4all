package model.tools.collectionTools.rose

import model.UserSolution
import model.points.Points
import model.tools.collectionTools.programming.{ProgDataType, ProgLanguage}
import model.tools.collectionTools.{ExerciseContent, SampleSolution}


final case class RoseExerciseContent(
  fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean,
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


final case class RoseUserSolution(id: Int, part: RoseExPart, language: ProgLanguage, solution: String, points: Points, maxPoints: Points)
  extends UserSolution[ String]
