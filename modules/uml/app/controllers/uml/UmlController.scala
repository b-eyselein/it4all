package controllers.uml

import java.io.IOException
import java.nio.file.{Files, Paths}

import controllers.core.{BaseController, ExerciseController}
import javax.inject.Inject
import model.{ClassSelectionResult, DiagramDrawingHelpResult, DiagramDrawingResult, UmlExPart, UmlExercise, UmlResult, UmlSolution}
import model.result.CompleteResult
import model.user.User
import play.Logger
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Result, Results}
import play.twirl.api.Html

class UmlController @Inject() (f: FormFactory)
  extends ExerciseController[UmlExercise, UmlResult](f, "uml", UmlExercise.finder, UmlToolObject) {

  val ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!"

  def classSelection(exerciseId: Int) =
    Results.ok(views.html.classSelection.render(BaseController.getUser, finder.byId(exerciseId)))

  def diagramDrawing(exerciseId: Int) =
    Results.ok(views.html.diagdrawing.render(BaseController.getUser, finder.byId(exerciseId), false))

  def diagramDrawingWithHelp(exerciseId: Int) =
    Results.ok(views.html.diagdrawing.render(BaseController.getUser, finder.byId(exerciseId), true))

  def matching(exerciseId: Int) =
    Results.ok(views.html.umlMatching.render(BaseController.getUser, finder.byId(exerciseId)))

  def correct(exerciseId: Int, partStr: String): Result = {
    val exercise = finder.byId(exerciseId)
    val solOption = UmlSolution.readFromForm(factory.form().bindFromRequest())

    val part = UmlExPart.valueOf(partStr)

    solOption match {
      case Some(sol) ⇒
        Results.ok(part match {
          case UmlExPart.CLASS_SELECTION ⇒
            val result = new ClassSelectionResult(exercise, sol)
            views.html.results.classSelectionSolution.render(BaseController.getUser, result)

          case UmlExPart.DIAG_DRAWING_HELP ⇒
            val result = new DiagramDrawingHelpResult(exercise, sol)
            views.html.results.diagdrawinghelpsol.render(BaseController.getUser, result)

          case UmlExPart.DIAG_DRAWING ⇒
            val result = new DiagramDrawingResult(exercise, sol)
            views.html.results.diagdrawingsol.render(BaseController.getUser, result)

          case UmlExPart.ATTRS_METHS ⇒
            views.html.results.umlMatchingCorrection.render(BaseController.getUser)

          case _ ⇒ new Html("TODO!")
        })
      case None ⇒ Results.badRequest("There has been an error!")
    }
  }

  override def correct(form: DynamicForm, exercise: UmlExercise, user: User) = //FIXME
    ???

  override def renderExercise(user: User, exercise: UmlExercise) = //FIXME
    ???

  val renderExesListRest = new Html(s"""
<div class="alert alert-info">
Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte 
der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[UmlResult]) = //FIXME
    ???
}

object UmlController {

  val SolutionSchemaNode = UmlController.initSolutionSchemaNode.get

  val SolutionSchemaPath = Paths.get("conf", "resources", "uml", "solutionSchema.json")

  def initSolutionSchemaNode = {
    try {
      Some(Json.parse(String.join("\n", Files.readAllLines(SolutionSchemaPath))))
    } catch {
      case e: IOException ⇒
        Logger.error("There has been an error parsing the schema files for UML:", e)
        None
    }
  }

}
