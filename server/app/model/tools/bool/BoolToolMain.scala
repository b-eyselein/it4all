package model.tools.bool

import javax.inject.{Inject, Singleton}
import model.core.result.EvaluationResult
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Boolesche Algebra", "bool") {

  private val logger = Logger(classOf[BoolToolMain])

  // Abstract types

  override type PartType = BoolExPart

  override type ResultType = EvaluationResult

  override type Tables = BoolTableDefs

  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  // Views

  override def exercisesOverviewForIndex: Html = Html("")

  // Correction

  def readSolution(exPart: BoolExPart, request: Request[AnyContent]): Either[scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])], BoolSolution] =
    request.body.asJson match {
      case None          => Left(???)
      case Some(jsValue) => BoolSolutionJsonFormat.boolSolutionReads.reads(jsValue).asEither
    }

  def checkSolution(exPart: BoolExPart, boolSolution: BoolSolution): JsValue =
    BoolCorrector.correctPart(exPart, boolSolution).toJson

}
