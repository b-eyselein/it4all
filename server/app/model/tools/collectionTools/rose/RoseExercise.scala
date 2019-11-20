package model.tools.collectionTools.rose

import model._
import model.points.Points
import model.tools.collectionTools.programming.{ProgDataType, ProgLanguage}


final case class RoseExercise(
  id: Int, collectionId: Int, toolId: String = RoseConsts.toolId, semanticVersion: SemanticVersion, title: String, author: String, text: LongText, state: ExerciseState,
  fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean, inputTypes: Seq[RoseInputType], sampleSolutions: Seq[RoseSampleSolution]
) extends Exercise {

  override protected type SolutionType = String
  override protected type SampleSolutionType = RoseSampleSolution

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

final case class RoseSampleSolution(id: Int, language: ProgLanguage, sample: String)
  extends SampleSolution[String]

final case class RoseUserSolution(id: Int, part: RoseExPart, language: ProgLanguage, solution: String, points: Points, maxPoints: Points)
  extends UserSolution[RoseExPart, String]

final case class RoseExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
