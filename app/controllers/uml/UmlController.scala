package controllers.uml

import java.nio.file.{Files, Path, Paths}
import javax.inject._

import com.fasterxml.jackson.databind.JsonNode
import controllers.core.excontrollers.IdPartExController
import model.User
import model.core._
import model.core.result.CompleteResult
import model.uml._
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.libs.Json
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                             (implicit ec: ExecutionContext)
  extends IdPartExController[UmlExercise, UmlResult](cc, dbcp, r, UmlToolObject) with Secured {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???


  override type TQ = repo.UmlExerciseTable

  override def tq = repo.umlExercises


  override def exercise(exerciseId: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      //      exById(exerciseId) match {
      //        case None           => BadRequest("TODO!")
      //        case Some(exercise) =>
      //          Ok(UmlExPart.valueOf(part) match {
      //            case UmlExPart.CLASS_SELECTION   => views.html.uml.classSelection.render(user, exercise)
      //            case UmlExPart.DIAG_DRAWING      => views.html.uml.diagdrawing.render(user, exercise, getsHelp = false)
      //            case UmlExPart.DIAG_DRAWING_HELP => views.html.uml.diagdrawing.render(user, exercise, getsHelp = true)
      //            case UmlExPart.ATTRS_METHS       => views.html.uml.umlMatching.render(user, exercise)
      //            case _                           => new Html("FEHLER!")
      //          })
      //      }
      Ok("TODO")

  }

  override def correct(exerciseId: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      //      exById(exerciseId) match {
      //        case None           => BadRequest("TODO!")
      //        case Some(exercise) =>
      //          Option(null) /*UmlSolution.readFromForm(null factory.form().bindFromRequest())*/ match {
      //            case None      => BadRequest("There has been an error!")
      //            case Some(sol) =>
      //              val (result, nextPart) = UmlExPart.valueOf(part) match {
      //                case UmlExPart.CLASS_SELECTION   => (ClassSelectionResult(exercise, sol), UmlExPart.DIAG_DRAWING_HELP)
      //                case UmlExPart.DIAG_DRAWING_HELP => (DiagramDrawingHelpResult(exercise, sol), UmlExPart.ATTRS_METHS)
      //                case UmlExPart.DIAG_DRAWING      => (DiagramDrawingResult(exercise, sol), UmlExPart.FINISHED)
      //                case UmlExPart.ATTRS_METHS       => (null, UmlExPart.FINISHED)
      //                case UmlExPart.FINISHED          => (null, UmlExPart.FINISHED)
      //              }
      //              Ok(views.html.uml.umlResult.render(user, result, nextPart))
      //          }
      //      }
      Ok("TODO!")
  }

  override def correctEx(sol: StringSolution, exercise: Option[UmlExercise], user: User): Try[CompleteResult[UmlResult]] = ??? //FIXME: not used...


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
