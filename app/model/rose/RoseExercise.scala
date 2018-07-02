package model.rose

import model._
import model.programming.ProgDataTypes.ProgDataType
import model.programming.ProgLanguage
import play.twirl.api.Html

// Classes for use

case class RoseCompleteEx(ex: RoseExercise, inputType: Seq[RoseInputType], sampleSolution: Seq[RoseSampleSolution]) extends PartsCompleteEx[RoseExercise, RoseExPart] {

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

case class RoseExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, semanticVersion: SemanticVersion,
                        fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends Exercise

case class RoseInputType(id: Int, exerciseId: Int, name: String, inputType: ProgDataType)

case class RoseSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

case class RoseSolution(username: String, exerciseId: Int, part: RoseExPart, solution: String, points: Double, maxPoints: Double) extends PartSolution[RoseExPart, String]

