package model.programming

import model._
import model.programming.ProgConsts._
import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html


// Classes for use

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                          sampleTestData: Seq[SampleTestData], maybeClassDiagramPart: Option[UmlClassDiagPart])
  extends PartsCompleteEx[ProgExercise, ProgrammingExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgrammingExPart): Boolean = partType match {
    case ProgrammingExParts.Implementation => true
    case ProgrammingExParts.ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case ProgrammingExParts.TestdataCreation => false
  }

  def addIndent(solution: String): String = solution split "\n" map (str => " " * (4 * ex.indentLevel) + str) mkString "\n"

}

// Case classes for tables

case class ProgExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, folderIdentifier: String, base: String,
                        functionname: String, indentLevel: Int, outputType: ProgDataType, baseData: Option[JsValue]) extends Exercise


case class ProgInput(id: Int, exerciseId: Int, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, base: String, solution: String)

sealed trait TestData {

  val id: Int
  val exerciseId: Int
  val inputAsJson: JsValue
  val output: String

}

trait TestDataInput {

  val id: Int
  val testId: Int
  val exerciseId: Int
  val input: String

}

case class SampleTestData(id: Int, exerciseId: Int, inputAsJson: JsValue, output: String) extends TestData

case class CommitedTestData(id: Int, exerciseId: Int, inputAsJson: JsValue, output: String, username: String, state: ExerciseState) extends TestData {

  def toJson: JsObject = Json.obj(
    idName -> id,
    exerciseIdName -> exerciseId,
    usernameName -> username,
    outputName -> output,
    stateName -> state.entryName,
    inputsName -> inputAsJson
  )

}

// Solution types

object TestDataSolution extends JsonFormat {

  def readTestDataFromJson(jsonStr: String): Seq[CommitedTestData] = Json.parse(jsonStr).asArray { jsValue =>

    for {
      jsObject <- jsValue.asObj
      id <- jsObject.intField(idName)
      exerciseId <- jsObject.intField(exerciseIdName)
      output <- jsObject.stringField(outputName)
      inputAsJson <- jsObject.value get "TODO!"
      username <- jsObject.stringField(usernameName)
      state <- jsObject.enumField(stateName, ExerciseState.withNameInsensitive)
    } yield CommitedTestData(id, exerciseId, inputAsJson, output, username, state)

  } getOrElse Seq.empty

}

object ProgSolution {

  def tupled(t: (String, Int, ProgrammingExPart, ProgLanguage, String)): ProgSolution = t._3 match {
    case ProgrammingExParts.Implementation => ImplementationSolution(t._1, t._2, t._4, t._5)
    case ProgrammingExParts.TestdataCreation => TestDataSolution(t._1, t._2, t._4, TestDataSolution.readTestDataFromJson(t._5))
    case ProgrammingExParts.ActivityDiagram => ActivityDiagramSolution(t._1, t._2, t._4, t._5)
  }

  def apply(username: String, exerciseId: Int, exercisePart: ProgrammingExPart, progLanguage: ProgLanguage, solutionStr: String): ProgSolution = exercisePart match {
    case ProgrammingExParts.Implementation => ImplementationSolution(username, exerciseId, progLanguage, solutionStr)
    case ProgrammingExParts.TestdataCreation => TestDataSolution(username, exerciseId, progLanguage, TestDataSolution.readTestDataFromJson(solutionStr))
    case ProgrammingExParts.ActivityDiagram => ActivityDiagramSolution(username, exerciseId, progLanguage, solutionStr)
  }

  def unapply(arg: ProgSolution): Option[(String, Int, ProgrammingExPart, ProgLanguage, String)] =
    Some(arg.username, arg.exerciseId, arg.part, arg.language, arg.solution)

}

sealed trait ProgSolution extends PartSolution[ProgrammingExPart] {

  val language: ProgLanguage

  def solution: String

}

case class ImplementationSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgrammingExPart = ProgrammingExParts.Implementation

}

case class TestDataSolution(username: String, exerciseId: Int, language: ProgLanguage, commitedTestData: Seq[CommitedTestData]) extends ProgSolution {

  override val part: ProgrammingExPart = ProgrammingExParts.TestdataCreation

  override def solution: String = "[" + commitedTestData.map(_.toJson).mkString(",") + "]"

}

case class ActivityDiagramSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgrammingExPart = ProgrammingExParts.ActivityDiagram

}