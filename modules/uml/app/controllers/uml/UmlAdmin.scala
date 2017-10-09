package controllers.uml

import scala.util.{ Failure, Success }

import controllers.core.{ AExerciseAdminController, BaseController }
import javax.inject.Inject
import model.{ StringConsts, UmlExTextParser, UmlExercise, UmlExerciseReader }
import model.exercisereading.JsonReader
import model.user.User
import play.data.FormFactory
import play.libs.Json
import play.mvc.Results

class UmlAdmin @Inject() (f: FormFactory)
  extends AExerciseAdminController[UmlExercise](f, UmlToolObject, UmlExercise.finder, UmlExerciseReader) {

  override def renderAdminIndex(user: User) = views.html.umlAdmin.index.render(user)

  def checkSolution() = {
    val form = factory.form().bindFromRequest()

    val solNode = Json.parse(form.get(StringConsts.SOLUTION_NAME))
    JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      case Success(report) => Results.ok("ok...")
      case Failure(_)      => Results.badRequest("FEHLER!")
    }
  }

  def newExerciseStep2 = {
    val exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest())
    val parser = new UmlExTextParser(exercise.text, exercise.mappings, exercise.ignoreWords)
    Results.ok(views.html.umlAdmin.newExerciseStep2Form.render(BaseController.getUser, exercise, parser.getCapitalizedWords))
  }

  def newExerciseStep3 = {
    val exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest())
    Results.ok(views.html.umlAdmin.newExerciseStep3Form.render(BaseController.getUser, exercise))
  }

  override def renderExEditForm(user: User, exercise: UmlExercise, isCreation: Boolean) = //FIXME
    ???
}
