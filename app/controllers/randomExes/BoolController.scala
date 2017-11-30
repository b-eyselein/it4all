package controllers.randomExes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import model.Enums.SuccessType.PARTIALLY
import model.JsonFormat
import model.core.Repository
import model.essentials.BoolNodeParser.parseBoolFormula
import model.essentials.BooleanQuestion._
import model.essentials.EssentialsConsts._
import model.essentials._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.libs.{Json => JavaJson}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

@Singleton
class BoolController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with JsonFormat {

  // Boolean Algebra

  def newBoolFilloutQuestion(opsAsSymbols: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.boolFilloutQuestion.render(user, generateNewFilloutQuestion, opsAsSymbols))
  }

  def checkBoolFilloutSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readFilloutQuestionResultFromJson match {
        case None                        => BadRequest("There has been an error!")
        case Some(filloutQuestionResult) =>

          val ret = JsArray(filloutQuestionResult.assignments map { assignment =>
            val fields: Seq[(String, JsValue)] = assignment.assignments map { case (variab, bool) => variab.asString -> JsBoolean(bool) } toSeq

            JsObject(fields ++ Option("id" -> JsString(assignment.identifier)))
          })

          Ok(ret)
      }
  }

  def newBoolCreationQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.essentials.boolcreatequestion.render(user, generateNewCreationQuestion)) }


  def checkBoolCreationSolution: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asFormUrlEncoded flatMap checkCreationSolution match {
        case None         => BadRequest("There has been an error!")
        case Some(result) => Ok(views.html.essentials.boolcreatesolution.render(user, result))
      }
  }

  def checkBoolCreationSolutionLive: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None => BadRequest("There has been an error!")

        case Some(data) =>
          val result = checkCreationSolution(data)
          // FIXME: ugly, stupid, sh**ty hack!
          Ok(play.api.libs.json.Json.parse(JavaJson.toJson(result.get).toString))
      }
  }

  // Helper methods

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

  private def checkCreationSolution(data: Map[String, Seq[String]]): Option[CreationQuestionResult] = {
    // FIXME: get per json, read from json!
    val learnerSolution = data(FORM_VALUE) mkString

    parseBoolFormula(learnerSolution) map { formula =>
      val variables = data(VARS_NAME).mkString split "," map (v => Variable(v(0))) toSet

      // Check that formula only contains variables found in form
      //      val wrongVars = formula.usedVariables filter (!variables.contains(_))
      //        if (wrongVars.nonEmpty)
      //          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

      val assignments = BoolAssignment
        .generateAllAssignments(variables toSeq)
        .map(as => as + (LerVariable -> formula(as)) + (SolVariable -> (data(as.toString).mkString == "1")))

      CreationQuestionResult(PARTIALLY, learnerSolution, CreationQuestion(variables, assignments))
    }
  }

}