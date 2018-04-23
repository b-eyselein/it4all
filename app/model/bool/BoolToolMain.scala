package model.bool

import javax.inject.{Inject, Singleton}
import model.Enums.ToolState
import model._
import model.bool.BoolConsts._
import model.bool.BooleanQuestion._
import model.core.EvaluationResult
import model.learningPath.{LearningPath, LearningPathYamlProtocol}
import model.toolMains.RandomExerciseToolMain
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.{Failure, Success}

@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext) extends RandomExerciseToolMain("bool") with JsonFormat {

  // Abstract types

  override type PartType = BoolExPart

  override type R = EvaluationResult

  override type Tables = BoolTableDefs

  // Other members

  override val toolname: String = "Boolesche Algebra"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = BoolConsts

  override val exParts: Seq[PartType] = BoolExParts.values

  // Views

  override def index(user: User, learningPathBases: Seq[LearningPath]): Html =
    views.html.bool.boolOverview(user, this, learningPathBases)

  override def newExercise(user: User, exType: BoolExPart, options: Map[String, Seq[String]]): Html = exType match {
    case FormulaCreation => views.html.bool.boolCreateQuestion(user, generateNewCreationQuestion, this)
    case TableFillout    => views.html.bool.boolFilloutQuestion(user, generateNewFilloutQuestion, this)
  }

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

  override def readLearningPaths: Seq[LearningPath] = readAll(exerciseResourcesFolder / "learningPath.yaml") match {
    case Failure(error)       => Seq.empty
    case Success(fileContent) =>
      LearningPathYamlProtocol.LearningPathYamlFormat.read(fileContent.parseYaml) match {
        case Failure(error) =>
          Logger.error("Fehler: ", error)
          Seq.empty
        case Success(read)  => Seq(read)
      }
  }

}