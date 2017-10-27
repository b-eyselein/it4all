package controllers.bool

import javax.inject._

import controllers.bool.BoolController._
import controllers.core.excontrollers.ARandomExController
import model.User
import model.bool.{BooleanQuestionResult, _}
import model.core.StringConsts.{FORM_VALUE, VARS_NAME}
import model.core.result.SuccessType
import model.core.{Repository, Secured}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

object BoolController {
  val ONE               = "1"
  val SOL_VAR: Variable = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR: Variable = BooleanQuestion.LEARNER_VARIABLE
}

class BoolController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                              (implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, BoolToolObject) with Secured {

  def checkBoolFilloutSolution: EssentialAction = withUser { user =>
    implicit request => //Controller.request.body.asFormUrlEncoded match {
      BadRequest("")

    //    case Some(data) =>
    //      val formulaOptional = ScalaNodeParser.parse(data(StringConsts.FORM_VALUE).mkString(""))
    //
    //      if (!formulaOptional.isDefined)
    //        BadRequest("There has been an error!")
    //
    //      val formula = formulaOptional.get
    //
    //      val assignments = Assignment.generateAllAssignments(formula.usedVariables.toList).map(assignment =>
    //        assignment + (LEA_VAR -> (ONE == data(assignment.toString).mkString(""))) + (SOL_VAR -> formula.evaluate(assignment)))
    //
    //      val question = new FilloutQuestion(formula, assignments)
    //
    //      ok(views.html.boolfilloutsolution.render(BaseController.user(), question))
    //  }
  }

  def newBoolCreationQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.bool.boolcreatequestion.render(user, CreationQuestion.generateNew)) }

  def newBoolFilloutQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.bool.boolfilloutquestion.render(user, FilloutQuestion.generateNew)) }

  def checkBoolCreationSolution: EssentialAction = withUser { user =>
    implicit request => //Controller.request().body.asFormUrlEncoded match {
      BadRequest("TODO!")

    //    case Some(data) =>
    //      try {
    //        var result = checkCreationSolution(data)
    //        ok(views.html.boolcreatesolution.render(BaseController.user(), result))
    //      } catch {
    //        case ce: CorrectionException => ok(views.html.error.render(BaseController.user(), ce))
    //      }
    //  }
  }

  def checkBoolCreationSolutionLive: EssentialAction = withUser { user =>
    implicit request => //Controller.request.body.asFormUrlEncoded match {
      BadRequest("TODO!")

    //    case Some(data) =>
    //      try {
    //        var result = checkCreationSolution(data)
    //        // FIXME: ugly, stupid, sh**ty hack!
    //        ok(play.api.libs.json.Json.parse(play.libs.Json.toJson(result).toString()))
    //      } catch {
    //        case ce: CorrectionException => null
    //      }
    //  }
  }

  private def checkCreationSolution(data: Map[String, Seq[String]]): BooleanQuestionResult = {
    val learnerSolution = data(FORM_VALUE).mkString
    ScalaNodeParser.parse(learnerSolution) match {
      case None =>
        new BooleanQuestionResult(SuccessType.NONE, learnerSolution, null)
      //        throw new CorrectionException(learnerSolution, "Formula could not be parsed!")
      case Some(formula) =>
        val variables = data(VARS_NAME).mkString.split(",").map(variab => Variable(variab.charAt(0))).toList

        // Check that formula only contains variables found in form
        val wrongVars = formula.usedVariables.filter(!variables.contains(_))
        //        if (wrongVars.nonEmpty)
        //          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

        val assignments = Assignment
          .generateAllAssignments(variables)
          .map(as => as + (LEA_VAR -> formula.evaluate(as)) + (SOL_VAR -> (data(as.toString()).mkString == ONE)))

        val question = new CreationQuestion(variables, assignments)
        new BooleanQuestionResult(SuccessType.PARTIALLY, learnerSolution, question)
    }
  }

  override def renderIndex(user: User): Html = views.html.bool.booloverview.render(user)

}
