package controllers.uml

import javax.inject.Inject

import controllers.core.{AExerciseAdminController, BaseController}
import model.exercisereading.JsonReader
import model.user.User
import model.{StringConsts, UmlExTextParser, UmlExercise, UmlExerciseReader}
import play.api.Configuration
import play.data.FormFactory
import play.libs.Json
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success}

class UmlAdmin @Inject()(c: Configuration, f: FormFactory)
  extends AExerciseAdminController[UmlExercise](c, f, new UmlToolObject(c), UmlExercise.finder, UmlExerciseReader) {

  def checkSolution: Result = {
    val form = factory.form().bindFromRequest()

    val solNode = Json.parse(form.get(StringConsts.SOLUTION_NAME))
    JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      case Success(_) => Results.ok("ok...")
      case Failure(_) => Results.badRequest("FEHLER!")
    }
  }

  def newExerciseStep2: Result = {
    val exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest())
    val parser = new UmlExTextParser(exercise.text, exercise.mappings, exercise.ignoreWords)
    Results.ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser, exercise, parser.getCapitalizedWords))
  }

  def newExerciseStep3: Result = {
    val exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest())
    Results.ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser, exercise))
  }

  override def renderExEditForm(user: User, exercise: UmlExercise, isCreation: Boolean): Html = ??? //FIXME

}
