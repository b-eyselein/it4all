package controllers.exes.idPartExes

import controllers.Secured
import controllers.exes.BaseExerciseController
import javax.inject.{Inject, Singleton}
import model._
import model.core._
import model.programming.{NewProgYamlProtocol, ProgLanguage}
import model.xml.{XmlCompleteResult, XmlCorrector}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import views.html.programming.testNewFormat

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AIdPartExController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: Repository)
                                   (implicit ec: ExecutionContext) extends BaseExerciseController(cc, dbcp) with Secured with JsonFormat {

  // Abstract types

  type SolType

  // Generic Routes

  def correct(tool: String, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = BaseExerciseController.IdPartExToolMains(tool)

      toolMain.correctAbstract(user, id, isLive = false) map {
        case Failure(error)  => BadRequest(toolMain.onSubmitCorrectionError(user, error))
        case Success(result) =>
          result match {
            case Right(jsValue) => Ok(jsValue)
            case Left(html)     => Ok(html)
          }
      }
  }

  def correctLive(tool: String, id: Int): EssentialAction = futureWithUser { user =>
    implicit request => {
      val toolMain = BaseExerciseController.IdPartExToolMains(tool)

      toolMain.correctAbstract(user, id, isLive = true) map {
        case Failure(error)  => BadRequest(toolMain.onLiveCorrectionError(error))
        case Success(result) => result match {
          case Right(jsValue) => Ok(jsValue)
          case Left(html)     => Ok(html)
        }
      }
    }
  }

  def exercise(tool: String, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      BaseExerciseController.IdPartExToolMains(tool).renderExerciseById(user, id, partStr) map {
        case Some(r) => Ok(r)
        case None    => NotFound(s"There is no such exercise with id '$id'")
      }
  }

  // Other routes

  def webPlayground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground(user)) }

  // Other routes

  def progNewTest: EssentialAction = withAdmin {
    admin =>
      implicit request => {
        NewProgYamlProtocol.testRead match {
          case Success((fileContent, classTest)) => Ok(testNewFormat.render(admin, fileContent, classTest))
          case Failure(error)                    => BadRequest("There has been an error: " + error.getMessage)
        }
      }
  }

  def progGetDeclaration(lang: String): EssentialAction = withUser {
    _ => implicit request => Ok(ProgLanguage.valueOf(lang).getOrElse(ProgLanguage.STANDARD_LANG).declaration)
  }

  def webSolution(username: String, id: Int, partUrlName: String): Action[AnyContent] = Action.async { implicit request =>
    val toolMain = BaseExerciseController.IdPartExToolMains("web")

    tables.userByName(username) flatMap {
      case None       => Future(BadRequest("No such solution!"))
      case Some(user) =>
        //        val part = toolMain.partTypeFromUrl(partUrlName) getOrElse HtmlPart
        // FIXME: implement!
        //        val oldSolution = toolMain.readOldSolution(user, exerciseId, part) map {
        //          case Some(solution) => solution.solution
        //          case None           => STANDARD_HTML
        //        }
        //
        //        oldSolution map (sol => Ok(new Html(sol)))
        ???
    }
  }

  def xmlPlayground: EssentialAction = withUser { user => implicit request => Ok(views.html.xml.xmlPlayground(user)) }

  def xmlPlaygroundCorrection = Action { implicit request =>
    Solution.stringSolForm.bindFromRequest.fold(
      _ => BadRequest("There has been an error!"),
      sol => {
        val correctionResult = XmlCorrector.correct(sol.learnerSolution, "")
        val result = XmlCompleteResult(sol.learnerSolution, solutionSaved = false, correctionResult)
        Ok(result.render)
      })
  }

}
