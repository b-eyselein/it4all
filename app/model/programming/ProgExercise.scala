package model.programming

import model._
import model.programming.ProgConsts._
import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html

// Classes for use

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                          sampleTestData: Seq[SampleTestData], maybeClassDiagramPart: Option[UmlClassDiagPart])
  extends PartsCompleteEx[ProgExercise, ProgExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgExPart): Boolean = partType match {
    case ProgExParts.Implementation  => true
    case ProgExParts.ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case ProgExParts.TestdataCreation => false
  }

  def addIndent(solution: String): String = solution split "\n" map (str => " " * (4 * ex.indentLevel) + str) mkString "\n"

}

// Case classes for tables

case class ProgExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, folderIdentifier: String, base: String,
                        functionname: String, indentLevel: Int, outputType: ProgDataType, baseData: Option[JsValue]) extends Exercise


case class ProgInput(id: Int, exerciseId: Int, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, base: String, solution: String)

sealed trait TestData {

  val id         : Int
  val exerciseId : Int
  val inputAsJson: JsValue
  val output     : String

}

trait TestDataInput {

  val id        : Int
  val testId    : Int
  val exerciseId: Int
  val input     : String

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

object DBProgSolutionHelper extends JsonFormat {

  def readTestDataFromJson(jsonStr: String): Seq[CommitedTestData] = Json.parse(jsonStr).asArray { jsValue =>
    // FIXME: use play json reads/writes!!!
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

  //  def tupled(t: (String, Int, ProgrammingExPart, ProgLanguage, String, Double, Double)): DBProgSolution = t._3 match {
  //    case ProgrammingExParts.TestdataCreation => TestDataSolution(t._1, t._2, t._3, t._4, readTestDataFromJson(t._5), t._6, t._7)
  //    case _                                   => ImplementationSolution(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  //  }

  //  def apply(username: String, exerciseId: Int, part: ProgrammingExPart, language: ProgLanguage, sol: ProgSolution,
  //            points: Double, maxPoints: Double): DBProgSolution = (part, sol) match {
  //    case (ProgrammingExParts.TestdataCreation, t: TestDataSolution) =>
  //      TestDataSolution(username, exerciseId, part, t.language, t.commitedTestData, points, maxPoints)
  //    case (_, i: ImplementationSolution)                             =>
  //      ImplementationSolution(username, exerciseId, part, i.language, i.solution, points, maxPoints)
  //  }

  //  def unapply(arg: DBProgSolution): Option[(String, Int, ProgrammingExPart, ProgLanguage, String, Double, Double)] =
  //    Some((arg.username, arg.exerciseId, arg.part, arg.language, arg.solution, arg.points, arg.maxPoints))

}


sealed trait ProgSolution {
  val language: ProgLanguage
}

case class ProgStringSolution(solution: String, language: ProgLanguage) extends ProgSolution

case class ProgTestDataSolution(testData: Seq[CommitedTestData], language: ProgLanguage) extends ProgSolution

case class /* sealed trait */ DBProgSolution(username: String, exerciseId: Int, part: ProgExPart, solution: ProgSolution, // language: ProgLanguage,
                                             points: Double, maxPoints: Double) /*extends DBProgSolution */ extends PartSolution[ProgExPart, ProgSolution] {

  //  val language: ProgLanguage

  //  def solution: String

  def commitedTestData: Seq[CommitedTestData] = Seq.empty

  //    solution match {
  //    case ProgTestDataSolution(td, _) => td
  //    case _                           => Seq.empty
  //  }

}

//case class TestDataSolution(username: String, exerciseId: Int, part: ProgrammingExPart, language: ProgLanguage, commitedTestData: Seq[CommitedTestData],
//                            points: Double, maxPoints: Double) extends DBProgSolution {
//
//  override val solution: String = "[" + commitedTestData.map(_.toJson).mkString(",") + "]"
//
//}
//
//case class ImplementationSolution(username: String, exerciseId: Int, part: ProgrammingExPart, language: ProgLanguage, solution: String,
//                                  points: Double, maxPoints: Double) extends DBProgSolution
//