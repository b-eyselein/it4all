package controllers.bool

import javax.inject.Inject
import controllers.core.BaseController
import model._
import model.StringConsts._
import model.user.User
import model.exercise.Success
import play.data.DynamicForm
import play.data.FormFactory
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.mvc.Security.Authenticated

@Authenticated(classOf[Secured])
class BoolController @Inject() (factory: FormFactory, cc: ControllerComponents) extends AbstractController(cc) {

  val ONE = "1"
  val SOL_VAR = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR = BooleanQuestion.LEARNER_VARIABLE

  def checkBoolCreationSolution() = Action { request =>
    request.body.asFormUrlEncoded match {
      case None => BadRequest("TODO!")
      case Some(data) => Ok(views.html.boolcreatesolution.render(getUser(request), checkCreationSolution(data)))
    }
  }

  def checkBoolCreationSolutionLive() = Action { request =>
    request.body.asFormUrlEncoded match {
      case None => BadRequest("TODO!")
      // FIXME: ugly, stupid, sh**ty hack!
      case Some(data) => Ok(play.api.libs.json.Json.parse(play.libs.Json.toJson(checkCreationSolution(data)).toString()))
    }
  }

  private def checkCreationSolution(data: Map[String, Seq[String]]): BooleanQuestionResult = {
    val learnerSolution = data(FORM_VALUE).mkString
    ScalaNodeParser.parse(learnerSolution) match {
      case None => throw new CorrectionException(learnerSolution, "Formula could not be parsed!")
      case Some(formula) =>
        val variables = data(VARS_NAME).mkString.split(",").map(variab => Variable(variab.charAt(0))).toList

        val assignments = Assignment
          .generateAllAssignments(variables)
          .map(as => as + (LEA_VAR -> formula.evaluate(as)) + (SOL_VAR -> (data(as.toString()).mkString == ONE)))

        val question = new CreationQuestion(variables, assignments)
        new BooleanQuestionResult(Success.PARTIALLY, learnerSolution, question)
    }
  }

  def checkBoolFilloutSolution() = Action { request =>
    request.body.asFormUrlEncoded match {
      case None => BadRequest("")
      case Some(data) =>
        val formulaOptional = ScalaNodeParser.parse(data(StringConsts.FORM_VALUE).mkString(""))

        if (!formulaOptional.isDefined)
          BadRequest("There has been an error!")

        val formula = formulaOptional.get

        val question = new FilloutQuestion(formula)

        question.assignments.foreach(assignment =>
          assignment + (LEA_VAR -> (ONE == data(assignment.toString).mkString(""))) + (SOL_VAR -> formula.evaluate(assignment)))

        Ok(views.html.boolfilloutsolution.render(getUser(request), question))
    }
  }

  def index() = Action { request => Ok(views.html.booloverview.render(getUser(request))) }

  def newBoolCreationQuestion() = Action { request => Ok(views.html.boolcreatequestion.render(getUser(request), CreationQuestion.generateNew())) }

  def newBoolFilloutQuestion() = Action { request => Ok(views.html.boolfilloutquestion.render(getUser(request), FilloutQuestion.generateNew())) }

  def getUser(request: Request[AnyContent]) = User.finder.byId(getUsername(request))

  def getUsername(request: Request[AnyContent]) = request.session(model.StringConsts.SESSION_ID_FIELD)

}
