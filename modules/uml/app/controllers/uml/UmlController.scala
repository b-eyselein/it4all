package controllers.uml

import java.nio.file.{Files, Paths}
import javax.inject.Inject

import controllers.core.{BaseController, IdExController}
import model.result.CompleteResult
import model.user.User
import model._
import play.api.Configuration
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Result, Results}
import play.twirl.api.Html

class UmlController @Inject()(c: Configuration, f: FormFactory)
  extends IdExController[UmlExercise, UmlResult](c, f, "uml", UmlExercise.finder, UmlToolObject
  ) {

  val ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!"

  def exercise(exerciseId: Int, partStr: String) = {
    val user = getUser
    val exercise = finder.byId(exerciseId)

    Results.ok(UmlExPart.valueOf(partStr) match {
      case UmlExPart.CLASS_SELECTION => views.html.classSelection.render(user, exercise)
      case UmlExPart.DIAG_DRAWING => views.html.diagdrawing.render(user, exercise, false)
      case UmlExPart.DIAG_DRAWING_HELP => views.html.diagdrawing.render(user, exercise, true)
      case UmlExPart.ATTRS_METHS => views.html.umlMatching.render(user, exercise)
      case _ => new Html("FEHLER!")
    })
  }

  def correct(exerciseId: Int, partStr: String): Result = {
    val exercise = finder.byId(exerciseId)
    val solOption = UmlSolution.readFromForm(factory.form().bindFromRequest())

    val part = UmlExPart.valueOf(partStr)

    solOption match {
      case Some(sol) =>
        val (result, nextPart) = part match {
          case UmlExPart.CLASS_SELECTION => (new ClassSelectionResult(exercise, sol), UmlExPart.DIAG_DRAWING_HELP)
          case UmlExPart.DIAG_DRAWING_HELP => (new DiagramDrawingHelpResult(exercise, sol), UmlExPart.ATTRS_METHS)
          case UmlExPart.DIAG_DRAWING => (new DiagramDrawingResult(exercise, sol), UmlExPart.FINISHED)
          case UmlExPart.ATTRS_METHS => (null, UmlExPart.FINISHED)
          case UmlExPart.FINISHED => (null, UmlExPart.FINISHED)
        }
        Results.ok(views.html.umlResult.render(getUser, result, nextPart))
      case None => Results.badRequest("There has been an error!")
    }
  }

  override def correctEx(form: DynamicForm, exercise: UmlExercise, user: User) = //FIXME: not used...
    ???

  override def renderExercise(user: User, exercise: UmlExercise) = //FIXME
    ???

  val renderExesListRest = new Html(
    s"""
<div class="alert alert-info">
Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte 
der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[UmlResult]) = //FIXME
    ???
}

object UmlController {

  val SolutionSchemaPath = Paths.get("conf", "resources", "uml", "solutionSchema.json")

  val SolutionSchemaNode = Json.parse(String.join("\n", Files.readAllLines(SolutionSchemaPath)))

}
