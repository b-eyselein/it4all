package controllers.uml

import java.nio.file.{Files, Path, Paths}
import javax.inject.Inject

import com.fasterxml.jackson.databind.JsonNode
import controllers.core.IdExController
import model._
import model.result.CompleteResult
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.util.Try

class UmlController @Inject()(f: FormFactory)
  extends IdExController[UmlExercise, UmlResult](f, UmlExercise.finder, UmlToolObject) {

  val ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!"

  def exercise(exerciseId: Int, partStr: String): Result = {
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
          case UmlExPart.CLASS_SELECTION => (ClassSelectionResult(exercise, sol), UmlExPart.DIAG_DRAWING_HELP)
          case UmlExPart.DIAG_DRAWING_HELP => (DiagramDrawingHelpResult(exercise, sol), UmlExPart.ATTRS_METHS)
          case UmlExPart.DIAG_DRAWING => (DiagramDrawingResult(exercise, sol), UmlExPart.FINISHED)
          case UmlExPart.ATTRS_METHS => (null, UmlExPart.FINISHED)
          case UmlExPart.FINISHED => (null, UmlExPart.FINISHED)
        }
        Results.ok(views.html.umlResult.render(getUser, result, nextPart))
      case None => Results.badRequest("There has been an error!")
    }
  }

  override def correctEx(form: DynamicForm, exercise: UmlExercise, user: User): Try[CompleteResult[UmlResult]] = ??? //FIXME: not used...


  override def renderExercise(user: User, exercise: UmlExercise): Html = ??? //FIXME


  val renderExesListRest = new Html(
    s"""
<div class="alert alert-info">
Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte 
der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[UmlResult]): Html = ??? //FIXME

}

object UmlController {

  val SolutionSchemaPath: Path = Paths.get("conf", "resources", "uml", "solutionSchema.json")

  val SolutionSchemaNode: JsonNode = Json.parse(String.join("\n", Files.readAllLines(SolutionSchemaPath)))

}
