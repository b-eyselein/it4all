package controllers.bool

import controllers.core.{ ARandomExController, BaseController }
import javax.inject.Inject
import model.{ Assignment, BooleanQuestion, BooleanQuestionResult, CorrectionException, CreationQuestion, FilloutQuestion, ScalaNodeParser }
import model.StringConsts.{ FORM_VALUE, VARS_NAME }
import model.Variable
import model.result.SuccessType
import model.user.User
import play.data.FormFactory
import play.mvc.Results.{ badRequest, ok }
import play.mvc.Security.Authenticated

@Authenticated(classOf[model.Secured])
class BoolController @Inject() (f: FormFactory) extends ARandomExController(f, BoolToolObject) {

  val ONE = "1"
  val SOL_VAR = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR = BooleanQuestion.LEARNER_VARIABLE

  def checkBoolCreationSolution() = //Controller.request().body.asFormUrlEncoded match {
    badRequest("TODO!")
  //    case Some(data) =>
  //      try {
  //        var result = checkCreationSolution(data)
  //        ok(views.html.boolcreatesolution.render(BaseController.getUser(), result))
  //      } catch {
  //        case ce: CorrectionException => ok(views.html.error.render(BaseController.getUser(), ce))
  //      }
  //  }

  def checkBoolCreationSolutionLive() = //Controller.request.body.asFormUrlEncoded match {
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
        if (!wrongVars.isEmpty)
          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

        val assignments = Assignment
          .generateAllAssignments(variables)
          .map(as => as + (LEA_VAR -> formula.evaluate(as)) + (SOL_VAR -> (data(as.toString()).mkString == ONE)))

        val question = new CreationQuestion(variables, assignments)
        new BooleanQuestionResult(SuccessType.PARTIALLY, learnerSolution, question)
    }
  }

  def checkBoolFilloutSolution() = //Controller.request.body.asFormUrlEncoded match {
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

  override def renderIndex(user: User) = views.html.booloverview.render(user)

  def newBoolCreationQuestion() = ok(views.html.boolcreatequestion.render(BaseController.getUser, CreationQuestion.generateNew()))

  def newBoolFilloutQuestion() = ok(views.html.boolfilloutquestion.render(BaseController.getUser, FilloutQuestion.generateNew()))

}
