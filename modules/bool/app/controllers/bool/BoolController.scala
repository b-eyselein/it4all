package controllers.bool

import javax.inject.Inject

import controllers.core.ARandomExController
import model.StringConsts.{FORM_VALUE, VARS_NAME}
import model._
import model.result.SuccessType
import model.user.User
import play.api.Configuration
import play.data.FormFactory
import play.mvc.Result
import play.mvc.Results.{badRequest, ok}
import play.mvc.Security.Authenticated
import play.twirl.api.Html

@Authenticated(classOf[model.Secured])
class BoolController @Inject()(c: Configuration, f: FormFactory) extends ARandomExController(c, f, new BoolToolObject(c)) {

  val ONE = "1"
  val SOL_VAR: Variable = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR: Variable = BooleanQuestion.LEARNER_VARIABLE

  def checkBoolCreationSolution(): Result = //Controller.request().body.asFormUrlEncoded match {
    badRequest("TODO!")

  //    case Some(data) =>
  //      try {
  //        var result = checkCreationSolution(data)
  //        ok(views.html.boolcreatesolution.render(BaseController.getUser(), result))
  //      } catch {
  //        case ce: CorrectionException => ok(views.html.error.render(BaseController.getUser(), ce))
  //      }
  //  }

  def checkBoolCreationSolutionLive(): Result = //Controller.request.body.asFormUrlEncoded match {
    badRequest("TODO!")

  //    case Some(data) =>
  //      try {
  //        var result = checkCreationSolution(data)
  //        // FIXME: ugly, stupid, sh**ty hack!
  //        ok(play.api.libs.json.Json.parse(play.libs.Json.toJson(result).toString()))
  //      } catch {
  //        case ce: CorrectionException => null
  //      }
  //  }

  private def checkCreationSolution(data: Map[String, Seq[String]]): BooleanQuestionResult = {
    val learnerSolution = data(FORM_VALUE).mkString
    ScalaNodeParser.parse(learnerSolution) match {
      case None => throw new CorrectionException(learnerSolution, "Formula could not be parsed!")
      case Some(formula) =>
        val variables = data(VARS_NAME).mkString.split(",").map(variab => Variable(variab.charAt(0))).toList

        // Check that formula only contains variables found in form
        val wrongVars = formula.usedVariables.filter(!variables.contains(_))
        if (wrongVars.nonEmpty)
          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

        val assignments = Assignment
          .generateAllAssignments(variables)
          .map(as => as + (LEA_VAR -> formula.evaluate(as)) + (SOL_VAR -> (data(as.toString()).mkString == ONE)))

        val question = new CreationQuestion(variables, assignments)
        new BooleanQuestionResult(SuccessType.PARTIALLY, learnerSolution, question)
    }
  }

  def checkBoolFilloutSolution: Result = //Controller.request.body.asFormUrlEncoded match {
    badRequest("")

  //    case Some(data) =>
  //      val formulaOptional = ScalaNodeParser.parse(data(StringConsts.FORM_VALUE).mkString(""))
  //
  //      if (!formulaOptional.isDefined)
  //        badRequest("There has been an error!")
  //
  //      val formula = formulaOptional.get
  //
  //      val assignments = Assignment.generateAllAssignments(formula.usedVariables.toList).map(assignment =>
  //        assignment + (LEA_VAR -> (ONE == data(assignment.toString).mkString(""))) + (SOL_VAR -> formula.evaluate(assignment)))
  //
  //      val question = new FilloutQuestion(formula, assignments)
  //
  //      ok(views.html.boolfilloutsolution.render(BaseController.getUser(), question))
  //  }

  override def renderIndex(user: User): Html = views.html.booloverview.render(user)

  def newBoolCreationQuestion: Result = ok(views.html.boolcreatequestion.render(getUser, CreationQuestion.generateNew))

  def newBoolFilloutQuestion: Result = ok(views.html.boolfilloutquestion.render(getUser, FilloutQuestion.generateNew))

}
