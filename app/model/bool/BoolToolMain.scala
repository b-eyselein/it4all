package model.bool

import javax.inject.{Inject, Singleton}
import model._
import model.bool.BoolConsts._
import model.bool.BooleanQuestion._
import model.core.result.EvaluationResult
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.Logger
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Boolesche Algebra", "bool") {

  // Abstract types

  override type PartType = BoolExPart

  override type R = EvaluationResult

  override type Tables = BoolTableDefs

  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[PartType] = BoolExParts.values

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a href="${controllers.routes.RandomExerciseController.newExercise(urlPart, BoolExParts.TableFillout.urlName)}" class="btn btn-primary btn-block">Wahrheitstabellen ausfüllen</a>
       |</div>
       |<div class="form-group">
       |  <a href="${controllers.routes.RandomExerciseController.newExercise(urlPart, BoolExParts.FormulaCreation.urlName)}" class="btn btn-primary btn-block">Erstellen einer Booleschen Formel</a>
       |</div>""".stripMargin)

  override def newExercise(user: User, exType: BoolExPart, options: Map[String, Seq[String]])
                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = exType match {
    case BoolExParts.FormulaCreation => views.html.randomExercises.bool.boolCreateQuestion(user, generateNewCreationQuestion, this)
    case BoolExParts.TableFillout    => views.html.randomExercises.bool.boolFilloutQuestion(user, generateNewFilloutQuestion, this)
  }

  override def playground(user: User): Html = views.html.randomExercises.bool.boolDrawing(user)

  // Handlers

  override def checkSolution(exPart: BoolExPart, request: Request[AnyContent]): JsValue = request.body.asJson match {
    case None          => Json.obj(errorName -> "There has been an error in your request!")
    case Some(jsValue) => BoolSolutionJsonFormat.boolSolutionReads.reads(jsValue) match {
      case JsSuccess(boolSolution, _) => correctPart(exPart, boolSolution).toJson
      case JsError(errors)            =>
        errors.foreach(e => Logger.error("Json Error: " + e.toString))
        Json.obj(errorName -> "There has been an error in your json!")
    }
  }

  private def correctPart(exPart: BoolExPart, boolSolution: BoolSolution): BooleanQuestionResult = {
    val formulaParseTry: Try[BoolNode] = BoolNodeParser.parseBoolFormula(boolSolution.formula)

    exPart match {
      case BoolExParts.TableFillout => formulaParseTry match {
        case Failure(_)       => FilloutQuestionError(boolSolution.formula, "There has been an internal error!")
        case Success(formula) => FilloutQuestionSuccess(formula, boolSolution.assignments map (as => as + (SolVariable -> formula(as))))
      }

      case BoolExParts.FormulaCreation => formulaParseTry match {
        case Failure(error)   => CreationQuestionError(boolSolution.formula, error.getMessage)
        case Success(formula) => CreationQuestionSuccess(formula, CreationQuestion(boolSolution.assignments))
      }
    }
  }

}