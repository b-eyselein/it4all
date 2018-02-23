package controllers.exes.idPartExes

import javax.inject._
import model.core._
import model.uml._
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import scalatags.Text.all._
import views.html.uml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: UmlTableDefs)(implicit ec: ExecutionContext)
  extends JsonFormat with AIdPartExToolMain[UmlExPart, UserUmlSolution, UmlExercise, UmlCompleteEx] {

  override val urlPart : String = "uml"
  override val toolname: String = "Uml"
  override val exType  : String = "uml"
  override val consts  : Consts = UmlConsts

  override def partTypeFromUrl(urlName: String): Option[UmlExPart] = UmlExParts.values.find(_.urlName == urlName)

  // Result types

  override type Tables = UmlTableDefs

  override type R = EvaluationResult

  override type CompResult = UmlResult

  // Reading solution

  override def saveSolution(sol: UserUmlSolution): Future[Boolean] = ???

  override def readOldSolution(user: User, exerciseId: Int, part: UmlExPart): Future[Option[UserUmlSolution]] = ???

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[UserUmlSolution] = {
    Solution.stringSolForm.bindFromRequest fold(
      _ => None,
      sol => {
        //        println(sol)
        UmlJsonProtocol.readUserUmlSolutionFromJson(Json parse sol.learnerSolution)
      })
  }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UserUmlSolution] = ???

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, maybePart: Option[UmlExPart]): Future[Html] = maybePart match {
    case Some(part) =>
      val html = part match {
        case ClassSelection     => classSelection.render(user, exercise.ex)
        case DiagramDrawing     => diagdrawing.render(user, exercise, getsHelp = false)
        case DiagramDrawingHelp => diagdrawing.render(user, exercise, getsHelp = true)
        case MemberAllocation   => allocation.render(user, exercise)
      }

      Future(html)
    case None       => ???
  }

  //  override protected val renderExesListRest = new Html(
  //    s"""<div class="alert alert-info">
  //       |  Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
  //       |</div>
  //       |<hr>""".stripMargin)

  private def renderResult(corResult: UmlResult): Html = {

    val resultsRender: String = corResult.notEmptyMatchingResults map (_.describe) mkString "\n"

    val nextPartLink: String = corResult.nextPart match {
      case Some(np) =>
        a(href := routes.AIdPartExController.exercise("uml", corResult.exercise.ex.id, np.urlName).url, cls := "btn btn-primary btn-block")("Zum nächsten Aufgabenteil").toString
      case None     =>
        a(href := routes.AIdPartExController.index("uml").url, cls := "btn btn-primary btn-block")("Zurück zur Startseite").toString
    }

    Html(resultsRender + "<hr>" + nextPartLink)
  }

  override def renderEditRest(exercise: Option[UmlCompleteEx]): Html = editUmlExRest.render(exercise)

  // Correction

  override def correctEx(user: User, sol: UserUmlSolution, exercise: UmlCompleteEx): Future[Try[UmlResult]] = Future {
    Try {
      sol.forPart match {
        case ClassSelection     => ClassSelectionResult(exercise, sol.solution)
        case DiagramDrawing     => DiagramDrawingResult(exercise, sol.solution)
        case DiagramDrawingHelp => DiagramDrawingHelpResult(exercise, sol.solution)
        case MemberAllocation   => AllocationResult(exercise, sol.solution)
      }
    }
  }

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: UmlResult): Html = views.html.core.correction.render(result, renderResult(result), user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = views.html.core.correctionError.render(user, OtherCorrectionException(error))


  override def onLiveCorrectionResult(result: UmlResult): JsValue = ??? // Ok(renderResult(result))

}