package model.bool

import javax.inject.Singleton
import model.Enums.ToolState
import model.bool.BoolConsts._
import model.bool.BooleanQuestion._
import model.core.EvaluationResult
import model.toolMains.RandomExerciseToolMain
import model.{Consts, JsonFormat, User}
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.language.implicitConversions

@Singleton
class BoolToolMain extends RandomExerciseToolMain("bool") with JsonFormat {

  // Abstract types

  override type PartType = BoolExPart

  override type R = EvaluationResult

  // Other members

  override val toolname: String = "Boolesche Algebra"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = BoolConsts

  override val exParts: Seq[PartType] = BoolExParts.values

  // Views

  override def newExercise(user: User, exType: BoolExPart, options: Map[String, Seq[String]]): Html = exType match {
    case FormulaCreation => views.html.bool.boolCreateQuestion(user, generateNewCreationQuestion, this)
    case TableFillout    => views.html.bool.boolFilloutQuestion(user, generateNewFilloutQuestion, this)
  }

  override def index(user: User): Html = views.html.bool.boolOverview(user, this)

  // Handlers

  override def checkSolution(user: User, exPart: BoolExPart, request: Request[AnyContent]): JsValue = {
    val correctionFunction: JsValue => Option[BooleanQuestionResult] = exPart match {
      case TableFillout    => readFillOutQuestionResultFromJson
      case FormulaCreation => readCreationQuestionResultFromJson
    }

    request.body.asJson flatMap correctionFunction match {
      case None         => Json.obj(errorName -> "TODO!")
      case Some(result) => result.toJson
    }
  }

  // Reading functions

  private def readAssignmentsObject(jsObject: JsObject): Option[BoolAssignment] = jsObject.asMap(_.apply(0), _.asBool) map {
    mapping => mapping map (strAndVal => (Variable(strAndVal._1), strAndVal._2))
  } map BoolAssignment.apply

  private def readFillOutQuestionResultFromJson(json: JsValue): Option[FilloutQuestionResult] = json.asObj flatMap { jsObj =>
    for {
      formula <- jsObj.stringField(FormulaName) flatMap BoolNodeParser.parseBoolFormula
      assignments <- jsObj.arrayField(AssignmentsName, _.asObj flatMap readAssignmentsObject)
    } yield FilloutQuestionResult(formula, assignments map (as => as + (SolVariable -> formula(as))))
  }

  private def readCreationQuestionResultFromJson(jsValue: JsValue): Option[CreationQuestionResult] = jsValue.asObj flatMap { jsObj =>
    for {
      learnerFormula <- jsObj.stringField(LearnerSol) flatMap BoolNodeParser.parseBoolFormula
      question <- jsObj.arrayField(AssignmentsName, _.asObj flatMap readAssignmentsObject) map CreationQuestion
      withSol <- jsObj.boolField("withSol")
    } yield CreationQuestionResult(learnerFormula, question, withSol)
  }

}