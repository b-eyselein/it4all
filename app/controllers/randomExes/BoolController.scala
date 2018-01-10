package controllers.randomExes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import model.JsonFormat
import model.core.Repository
import model.essentials.BoolAssignment._
import model.essentials.BoolNodeParser.parseBoolFormula
import model.essentials.BooleanQuestion._
import model.essentials.EssentialsConsts._
import model.essentials._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile
import views.html.essentials._

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

@Singleton
class BoolController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Repository)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with JsonFormat {

  // Table fillout

  def newBoolFilloutQuestion(opsAsSymbols: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(boolFilloutQuestion(user, generateNewFilloutQuestion, opsAsSymbols))
  }

  def checkBoolFilloutSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readFilloutQuestionResultFromJson match {
        case None                        => BadRequest("There has been an error!")
        case Some(filloutQuestionResult) =>
          Ok(JsArray(filloutQuestionResult.assignments map { assignment =>
            val fields: Seq[(String, JsValue)] = assignment.assignments map { case (variab, bool) => variab.asString -> JsBoolean(bool) } toSeq

            JsObject(fields ++ Option("id" -> JsString(assignment.identifier)))
          }))
      }
  }

  private def readFilloutQuestionResultFromJson(json: JsValue): Option[FilloutQuestionResult] = json.asObj flatMap { jsObj =>

    val maybeFormula: Option[ScalaNode] = jsObj.stringField(FormulaName) flatMap parseBoolFormula

    val maybeAssignments: Option[Seq[BoolAssignment]] = jsObj.arrayField(AssignmentsName, _.asObj flatMap readAssignmentsObject)

    (maybeFormula zip maybeAssignments).headOption map {
      case (formula, assignments) => FilloutQuestionResult(formula, assignments map (as => as + (SolVariable -> formula(as))))
    }
  }

  private def readAssignmentsObject(jsObject: JsObject): Option[BoolAssignment] = jsObject.asMap(_.apply(0), _.asBool) map {
    mapping => mapping map (strAndVal => (Variable(strAndVal._1), strAndVal._2))
  } map BoolAssignment.apply

  // Creation of a function

  def newBoolCreationQuestion: EssentialAction = withUser { user =>
    implicit request => Ok(boolCreateQuestion(user, generateNewCreationQuestion))
  }

  def checkBoolCreationSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readCreationQuestionResultFromJson match {
        case None         => BadRequest("There has been an error!")
        case Some(result) =>
          Ok(JsObject(Map(
            "assignments" -> JsArray(result.assignments map { as =>
              val learnerVal = result.learnerSolution(as)
              JsObject(Map("id" -> JsString(as.identifier), "learnerVal" -> JsBoolean(learnerVal), "correct" -> JsBoolean(as(SolVariable) == learnerVal)))
            }),
            "muster" -> JsObject(Map(
              "knf" -> JsString(disjunktiveNormalForm(result.assignments) asString),
              "dnf" -> JsString(konjunktiveNormalForm(result.assignments) asString))
            )))
          )
      }
  }

  private def readCreationQuestionResultFromJson(jsValue: JsValue): Option[CreationQuestionResult] = jsValue.asObj flatMap { jsObj =>

    val maybeLearnerFormula: Option[ScalaNode] = jsObj.stringField(LearnerSol) flatMap parseBoolFormula

    val maybeQuestion: Option[CreationQuestion] = jsObj.arrayField(AssignmentsName, _.asObj flatMap readAssignmentsObject) map CreationQuestion

    val maybeWithSol: Option[Boolean] = jsObj.boolField("withSol")

    (maybeLearnerFormula zip maybeQuestion zip maybeWithSol).headOption map {
      case ((learnerFormula, question), withSol) =>
        CreationQuestionResult(learnerFormula, question, withSol)
    }
  }

}