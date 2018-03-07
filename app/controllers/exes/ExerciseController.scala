package controllers.exes

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model._
import model.core._
import model.programming.{NewProgYamlProtocol, ProgLanguage, ProgrammingToolMain}
import model.toolMains.ToolList
import model.web.{HtmlPart, WebSolution, WebToolMain}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws._
import play.api.mvc._
import views.html.programming.testNewFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository, ws: WSClient,
                                   progToolMain: ProgrammingToolMain, webToolMain: WebToolMain)
                                  (implicit ec: ExecutionContext) extends SingleExerciseController(cc, dbcp) with Secured with JsonFormat {

  // Abstract types

  type SolType <: Solution

  // Generic Routes

  def exercise(tool: String, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getIdPartToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.renderExerciseById(user, id, partStr) map {
          case Some(r) => Ok(r)
          case None    => NotFound(s"There is no such exercise with id '$id'")
        }
      }
  }

  // FIXME: part in url!
  def correct(tool: String, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getIdPartToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.correctAbstract(user, id, partStr, isLive = false) map {
          case Failure(error)  => BadRequest(toolMain.onSubmitCorrectionError(user, error))
          case Success(result) =>
            result match {
              case Right(jsValue) => Ok(jsValue)
              case Left(html)     => Ok(html)
            }
        }
      }

  }

  def correctLive(tool: String, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request => {
      ToolList.getIdPartToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.correctAbstract(user, id, partStr, isLive = true) map {
          case Failure(error)  => BadRequest(toolMain.onLiveCorrectionError(error))
          case Success(result) => result match {
            case Right(jsValue) => Ok(jsValue)
            case Left(html)     => Ok(html)
          }
        }
      }
    }
  }

  // Other routes

  def progNewTest: EssentialAction = withAdmin { admin =>
    implicit request => {
      NewProgYamlProtocol.testRead(progToolMain.exerciseResourcesFolder) match {
        case Success((fileContent, classTest)) => Ok(testNewFormat.render(admin, fileContent, classTest))
        case Failure(error)                    => BadRequest("There has been an error: " + error.getMessage)
      }
    }
  }

  def progGetDeclaration(lang: String): EssentialAction = withUser {
    _ => implicit request => Ok(ProgLanguage.valueOf(lang).getOrElse(ProgLanguage.STANDARD_LANG).declaration)
  }

  def webSolution(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      ws.url(s"http://localhost:9080/${user.username}/$id/test.html").get() map { wsRequest =>
        Ok(wsRequest.body).as("text/html")
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
