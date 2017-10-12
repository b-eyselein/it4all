package controllers.uml

import javax.inject.Inject
import scala.collection.JavaConverters._
import controllers.core.AExerciseAdminController
import model.exercisereading.JsonReader
import model.user.User
import model.{StringConsts, UmlExTextParser, UmlExercise, UmlExerciseReader}
import play.data.FormFactory
import play.libs.Json
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success}

class UmlAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[UmlExercise](f, UmlToolObject, UmlExercise.finder, UmlExerciseReader) {

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
    val parser = new UmlExTextParser(exercise.text, exercise.mappings.asScala.toMap, exercise.ignoreWords.asScala.toList)
    Results.ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser, exercise, parser.capitalizedWords.toList))
  }

  def newExerciseStep3: Result = {
    val exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest())
    Results.ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser, exercise))
  }

  override def renderExEditForm(user: User, exercise: UmlExercise, isCreation: Boolean): Html = ??? //FIXME

}
