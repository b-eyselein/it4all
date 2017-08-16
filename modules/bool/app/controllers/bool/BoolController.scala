package controllers.bool

import javax.inject.Inject
import controllers.core.BaseController
import model._
import model.user.User
import model.exercise.Success
import play.data.DynamicForm
import play.data.FormFactory
import play.api.mvc._

class BoolController @Inject() (factory: FormFactory, cc: ControllerComponents) extends AbstractController(cc) {

  val ONE = "1"

  def checkBoolCreationSolution() = Action { request =>
    val form = factory.form().bindFromRequest()

    val learnerSolution = form.get(StringConsts.FORM_VALUE)
    val allVars = form.get("vars")

    val variables = allVars.split(",").map { variable: String => variable charAt 0 }.map(Variable(_)).toList

    val assignments = Assignment.generateAllAssignments(variables)

    assignments.foreach({ as => as + (BooleanQuestion.SOLUTION_VARIABLE -> (form.get(as.toString()) == ONE)) })

    val question = new CreationQuestion(variables, assignments)

    val formulaOptional = ScalaNodeParser.parse(learnerSolution)

    if (!formulaOptional.isDefined)
      BadRequest("There has been an error!")

    val formula = formulaOptional.get

    question.solutions.foreach(as => as + (BooleanQuestion.LEARNER_VARIABLE -> formula.evaluate(as)))

    val result = new BooleanQuestionResult(Success.PARTIALLY, learnerSolution, question)

    if (request.accepts("application/json"))
      // FIXME: ugly, sh**ty, stupid hack!
      Ok(play.api.libs.json.Json.parse(play.libs.Json.toJson(result).toString()))
    else
      Ok(views.html.boolcreatesolution.render(getUser(request), question))

  }

  def checkBoolFilloutSolution() = Action { request =>
    val form: DynamicForm = factory.form().bindFromRequest()

    val learnerSolution = form.get(StringConsts.FORM_VALUE)

    val formulaOptional = ScalaNodeParser.parse(learnerSolution)

    if (!formulaOptional.isDefined)
      BadRequest("There has been an error!")

    val formula = formulaOptional.get

    val question = new FilloutQuestion(formula)

    question.assignments.foreach({ assignment =>
      assignment + (BooleanQuestion.SOLUTION_VARIABLE -> formula.evaluate(assignment))
      assignment + (BooleanQuestion.LEARNER_VARIABLE -> ONE.equals(form.get(assignment.toString())))
    })

    Ok(views.html.boolfilloutsolution.render(getUser(request), question))
  }

  def index() = Action { request => Ok(views.html.booloverview.render(getUser(request))) }

  def newBoolCreationQuestion() = Action { request => Ok(views.html.boolcreatequestion.render(getUser(request), CreationQuestion.generateNew())) }

  def newBoolFilloutQuestion() = Action { request => Ok(views.html.boolfilloutquestion.render(getUser(request), FilloutQuestion.generateNew())) }

  def getUser(request: Request[AnyContent]) = User.finder.byId(getUsername(request))

  def getUsername(request: Request[AnyContent]) = request.session(model.StringConsts.SESSION_ID_FIELD)

}
