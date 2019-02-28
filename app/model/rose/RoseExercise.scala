package model.rose

import model._
import model.programming.{ProgDataType, ProgLanguage}
import play.twirl.api.Html

final case class RoseCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class RoseExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean, inputTypes: Seq[RoseInputType], sampleSolutions: Seq[RoseSampleSolution])
  extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.rose.rosePreview.render(this)

  def declaration(forUser: Boolean): String = {
    val className = if (forUser) "UserRobot" else "SampleRobot"
    val (methodName, returnType) = if (isMultiplayer) ("act", "Action") else ("run", "None")

    val parameters = inputTypes match {
      case Seq() => ""
      case other => ", " + (other map (it => it.name + ": " + it.inputType.typeName) mkString ", ")
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

final case class RoseExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: RoseExPart, difficulty: Difficulty, maybeDuration: Option[Int])
  extends ExerciseReview[RoseExPart]
