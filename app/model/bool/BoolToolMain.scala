package model.bool

import javax.inject.{Inject, Singleton}
import model._
import model.bool.BoolConsts._
import model.bool.BooleanQuestion._
import model.core.result.EvaluationResult
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext) extends RandomExerciseToolMain("bool") with JsonFormat {

  // Abstract types

  override type PartType = BoolExPart

  override type R = EvaluationResult

  override type Tables = BoolTableDefs

  // Other members

  override val hasPlayground: Boolean = true

  override val toolname: String = "Boolesche Algebra"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = BoolConsts

  override val exParts: Seq[PartType] = BoolExParts.values

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a href="${controllers.routes.RandomExerciseController.newExercise(urlPart, TableFillout.urlName)}" class="btn btn-primary btn-block">Wahrheitstabellen ausfüllen</a>
       |</div>
       |<div class="form-group">
       |  <a href="${controllers.routes.RandomExerciseController.newExercise(urlPart, FormulaCreation.urlName)}" class="btn btn-primary btn-block">Erstellen einer Booleschen Formel</a>
       |</div>""".stripMargin)

  override def newExercise(user: User, exType: BoolExPart, options: Map[String, Seq[String]]): Html = exType match {
    case FormulaCreation => views.html.randomExercises.bool.boolCreateQuestion(user, generateNewCreationQuestion, this)
    case TableFillout    => views.html.randomExercises.bool.boolFilloutQuestion(user, generateNewFilloutQuestion, this)
  }

  override def playground(user: User): Html = views.html.randomExercises.bool.boolDrawing(user)

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