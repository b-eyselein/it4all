package model.rose

import model._
import model.programming.{ProgDataType, ProgLanguage}
import play.twirl.api.Html

// Classes for use

final case class RoseCompleteEx(ex: RoseExercise, inputTypes: Seq[RoseInputType], sampleSolutions: Seq[RoseSampleSolution]) extends SingleCompleteEx[RoseExercise, RoseExPart] {

  // remaining field from RoseExercise

  def fieldWidth: Int = ex.fieldHeight

  def fieldHeight: Int = ex.fieldHeight

  def isMultiplayer: Boolean = ex.isMultiplayer

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.rose.rosePreview.render(this)

  override def hasPart(partType: RoseExPart): Boolean = true

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

// Case classes for tables

final case class RoseExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends Exercise

final case class RoseInputType(id: Int, exerciseId: Int, exSemVer: SemanticVersion, name: String, inputType: ProgDataType)

final case class RoseSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion, language: ProgLanguage, solution: String)

final case class RoseSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, part: RoseExPart,
                              solution: String, points: Points, maxPoints: Points) extends DBPartSolution[RoseExPart, String]

final case class RoseExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: RoseExPart,
                                    difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[RoseExPart]