package model.tools.bool

import javax.inject.{Inject, Singleton}
import model._
import model.core.result.EvaluationResult
import model.toolMains.{RandomExerciseToolMain, ToolState}
import model.tools.bool.BooleanQuestion._
import play.api.Logger
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Boolesche Algebra", "bool") {

  private val logger = Logger(classOf[BoolToolMain])

  // Abstract types

  override type SolutionType = BoolSolution

  override type PartType = BoolExPart

  override type ResultType = EvaluationResult

  override type Tables = BoolTableDefs

  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[PartType] = BoolExParts.values

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a href="${controllers.coll.routes.RandomExerciseController.newExercise(urlPart, BoolExParts.TableFillout.urlName)}" class="btn btn-primary btn-block">Wahrheitstabellen ausfüllen</a>
       |</div>
       |<div class="form-group">
       |  <a href="${controllers.coll.routes.RandomExerciseController.newExercise(urlPart, BoolExParts.FormulaCreation.urlName)}" class="btn btn-primary btn-block">Erstellen einer Booleschen Formel</a>
       |</div>""".stripMargin)

  override def newExercise(user: User, exType: BoolExPart, options: Map[String, Seq[String]])
                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = exType match {
    case BoolExParts.FormulaCreation => views.html.toolViews.bool.boolCreateQuestion(user, generateNewCreationQuestion, this)
    case BoolExParts.TableFillout    => views.html.toolViews.bool.boolFilloutQuestion(user, generateNewFilloutQuestion, this)
  }

  override def playground(user: User): Html = views.html.toolViews.bool.boolDrawing(user)

  // Handlers

  override def readSolution(exPart: BoolExPart, request: Request[AnyContent]): Either[Seq[(JsPath, Seq[JsonValidationError])], BoolSolution] =
    request.body.asJson match {
      case None          => Left(???)
      case Some(jsValue) => BoolSolutionJsonFormat.boolSolutionReads.reads(jsValue).asEither
    }

  override def checkSolution(exPart: BoolExPart, boolSolution: BoolSolution): JsValue =
    BoolCorrector.correctPart(exPart, boolSolution).toJson

}
