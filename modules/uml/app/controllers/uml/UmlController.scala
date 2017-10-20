package controllers.uml

import java.nio.file.{Files, Path, Paths}
import javax.inject.Inject

import com.fasterxml.jackson.databind.JsonNode
import controllers.excontrollers.IdPartExController
import model._
import model.result.CompleteResult
import model.user.User
import play.api.data.Form
import play.api.mvc.ControllerComponents
import play.libs.Json
import play.twirl.api.Html

import scala.util.Try

class UmlController @Inject()(cc: ControllerComponents)
  extends IdPartExController[UmlExercise, UmlResult](cc, UmlExercise.finder, UmlToolObject) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  override def exercise(exerciseId: Int, part: String) = Action { implicit request => {
    val user = getUser
    val exercise = finder.byId(exerciseId)

    Ok(UmlExPart.valueOf(part) match {
      case UmlExPart.CLASS_SELECTION => views.html.classSelection.render(user, exercise)
      case UmlExPart.DIAG_DRAWING => views.html.diagdrawing.render(user, exercise, false)
      case UmlExPart.DIAG_DRAWING_HELP => views.html.diagdrawing.render(user, exercise, true)
      case UmlExPart.ATTRS_METHS => views.html.umlMatching.render(user, exercise)
      case _ => new Html("FEHLER!")
    })
  }
  }

  override def correct(exerciseId: Int, part: String) = Action { implicit request => {
    val exercise = finder.byId(exerciseId)
    Option(null) /*UmlSolution.readFromForm(null factory.form().bindFromRequest())*/ match {
      case None => BadRequest("There has been an error!")
      case Some(sol) =>
        val (result, nextPart) = UmlExPart.valueOf(part) match {
          case UmlExPart.CLASS_SELECTION => (ClassSelectionResult(exercise, sol), UmlExPart.DIAG_DRAWING_HELP)
          case UmlExPart.DIAG_DRAWING_HELP => (DiagramDrawingHelpResult(exercise, sol), UmlExPart.ATTRS_METHS)
          case UmlExPart.DIAG_DRAWING => (DiagramDrawingResult(exercise, sol), UmlExPart.FINISHED)
          case UmlExPart.ATTRS_METHS => (null, UmlExPart.FINISHED)
          case UmlExPart.FINISHED => (null, UmlExPart.FINISHED)
        }
        Ok(views.html.umlResult.render(getUser, result, nextPart))
    }
  }
  }

  override def correctEx(sol: StringSolution, exercise: UmlExercise, user: User): Try[CompleteResult[UmlResult]] = ??? //FIXME: not used...


  override def renderExercise(user: User, exercise: UmlExercise): Html = ??? //FIXME


  override val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte
       |der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""")

  override def renderResult(correctionResult: CompleteResult[UmlResult]): Html = ??? //FIXME

}

object UmlController {

  val SolutionSchemaPath: Path = Paths.get("conf", "resources", "uml", "solutionSchema.json")

  val SolutionSchemaNode: JsonNode = Json.parse(String.join("\n", Files.readAllLines(SolutionSchemaPath)))

}
