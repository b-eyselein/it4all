package controllers

import javax.inject.{Inject, Singleton}
import model._
import model.core._
import model.programming.ProgrammingToolMain
import model.toolMains.{IdExerciseToolMain, ToolList}
import model.uml._
import model.web.{HtmlPart, WebSolution, WebToolMain}
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository, ws: WSClient,
                                   progToolMain: ProgrammingToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain)
                                  (implicit ec: ExecutionContext) extends ASingleExerciseController(cc, dbcp) with Secured with JsonFormat {

  // Abstract types

  type SolType <: Solution

  override type ToolMainType = IdExerciseToolMain

  override protected def getToolMain(toolType: String): Option[IdExerciseToolMain] = ToolList.getExerciseToolMainOption(toolType)

  // Generic Routes

  def exercise(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val maybeExAndMaybeOldSolution: Future[(Option[toolMain.CompExType], Option[toolMain.PartType], Option[toolMain.SolType])] = for {
        exercise <- toolMain.futureCompleteExById(id)
        part <- Future(toolMain.partTypeFromUrl(partStr))
        oldSolution <- toolMain.futureOldSolution(user, id, partStr)
      } yield (exercise, part, oldSolution)

      maybeExAndMaybeOldSolution map {
        case (None, _, _)                             => NotFound(s"Es gibt keine Aufgabe $id")
        case (Some(exercise), maybePart, oldSolution) => maybePart match {
          case None       => BadRequest(s"Es gibt keinen Aufgabenteil '$partStr'")
          case Some(part) =>
            //            if (exercise.hasPart(part))
            Ok(toolMain.renderExercise(user, exercise, part, oldSolution))
          //            else
          // BadRequest(s"Diese Aufgabe hat keinen Teil '${part.urlName}'")
        }
      }
  }

  // FIXME: part in url!
  def correct(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.correctAbstract(user, id, partStr, isLive = false) map {
        case Failure(error)  => BadRequest(toolMain.onSubmitCorrectionError(user, error))
        case Success(result) =>
          result match {
            case Right(jsValue) => Ok(jsValue)
            case Left(html)     => Ok(html)
          }
      }
  }

  def correctLive(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.correctAbstract(user, id, partStr, isLive = true) map {
        case Failure(error)  =>
          Logger.error("There has been an internal correction error:", error)
          BadRequest(toolMain.onLiveCorrectionError(error))
        case Success(result) => result match {
          case Right(jsValue) => Ok(jsValue)
          case Left(html)     => Ok(html)
        }
      }
  }

  // Views

  override protected def adminIndexView(admin: User, stats: Html, toolMain: IdExerciseToolMain): Html =
    views.html.admin.idExes.idExerciseAdminMain(admin, stats, toolMain)

  // Other routes

  def umlClassDiag(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      val futureClassDiagram: Future[UmlClassDiagram] = umlToolMain.futureOldSolution(user, id, partStr) flatMap {
        case Some(solution) => Future(solution.classDiagram)
        case None           => umlToolMain.partTypeFromUrl(partStr) match {
          case None       => Future(UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty))
          case Some(part) => umlToolMain.futureCompleteExById(id) map {
            case Some(exercise: UmlCompleteEx) => exercise.getDefaultClassDiagForPart(part)
            case None                          =>
              Logger.error(s"Error while loading uml class diagram for uml exercise $id and part $part")
              UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty)
          }
        }
      }

      futureClassDiagram map { classDiagram =>
        Ok("let defaultSol = " + Json.prettyPrint(UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(classDiagram))).as("text/javascript")
      }
  }

  def progClassDiagram(id: Int): EssentialAction = futureWithUser { _ =>
    implicit request =>
      progToolMain.futureCompleteExById(id) map {
        case None           => BadRequest
        case Some(exercise) => Ok(exercise.maybeClassDiagramPart.map(_.printToJavascript).getOrElse("")).as("text/javascript")
      }
  }

  def webSolution(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      webToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(BadRequest(s"There is no such part $partStr"))
        case Some(part) => ws.url(webToolMain.getSolutionUrl(user, id, part)).get() map (wsRequest => Ok(wsRequest.body).as("text/html"))
      }
  }

  def updateWebSolution(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      request.body.asText match {
        case None       => Future(BadRequest("No content!"))
        case Some(text) =>
          val webSol = WebSolution(user.username, id, webToolMain.partTypeFromUrl(part).getOrElse(HtmlPart), text)
          val futureSolSaved = webToolMain.futureSaveSolution(webSol)

          futureSolSaved map { solSaved =>
            if (solSaved) Ok("Solution saved")
            else BadRequest("Solution was not saved!")
          }
      }
  }

  def webPlayground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground(user)) }

  def xmlPlayground: EssentialAction = withUser { user => implicit request => Ok(views.html.xml.xmlPlayground(user)) }

}
