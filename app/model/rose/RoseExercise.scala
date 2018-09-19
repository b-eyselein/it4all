package model.rose

import model._
import model.programming.ProgDataTypes.ProgDataType
import model.programming.ProgLanguage
import play.twirl.api.Html

// Classes for use

final case class RoseCompleteEx(ex: RoseExercise, inputType: Seq[RoseInputType], sampleSolution: Seq[RoseSampleSolution]) extends SingleCompleteEx[RoseExercise, RoseExPart] {

  val NewLine = "\n"

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.rose.rosePreview.render(this)

  override def hasPart(partType: RoseExPart): Boolean = true

  def declaration(forUser: Boolean): String = {
    val className = if (forUser) "UserRobot" else "SampleRobot"
    val (actorClass, methodName, returnType) = if (ex.isMultiplayer) ("MultiPlayerActor", "act", "Action") else ("SinglePlayerActor", "run", "None")

    val parameters = inputType match {
      case Nil   => ""
      case other => ", " + (other map (it => it.name + ": " + it.inputType.typeName) mkString ", ")
    }

    s"""class $className(Robot, $actorClass):
       |  def $methodName(self$parameters) -> $returnType:""".stripMargin
  }

  def imports: String = if (ex.isMultiplayer) {
    """from typing import Dict
      |from base.actors import MultiPlayerActor
      |from base.actions import *
      |from base.robot import Robot""".stripMargin
  } else {
    """from typing import Dict
      |from base.actors import SinglePlayerActor
      |from base.actions import *
      |from base.robot import Robot""".stripMargin
  }

  def buildSampleSolution(language: ProgLanguage): String = {
    val sampleSol = sampleSolution.find(_.language == language) map (_.solution) getOrElse ""

    declaration(false) + NewLine + sampleSol.split(NewLine).map(" " * 4 + _).mkString(NewLine)
  }

}

// Case classes for tables

final case class RoseExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends Exercise

final case class RoseInputType(id: Int, exerciseId: Int, exSemVer: SemanticVersion, name: String, inputType: ProgDataType)

final case class RoseSampleSolution(exerciseId: Int, exSemVer: SemanticVersion, language: ProgLanguage, solution: String)

final case class RoseSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: RoseExPart,
                              solution: String, points: Points, maxPoints: Points) extends DBPartSolution[RoseExPart, String]

final case class RoseExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: RoseExPart,
                                    difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[RoseExPart]