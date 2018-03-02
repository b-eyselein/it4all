package controllers.exes

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.core._
import model.spread.SpreadConsts
import model.toolMains.ToolList
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class FileExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: Repository)
                                      (implicit ec: ExecutionContext) extends SingleExerciseController(cc, dbcp) with Secured with FileUtils {

  // Routes

  def exercise(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getFileToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.renderExerciseById(user, id, fileExtension) map {
          case Some(r) => Ok(r)
          case None    => Redirect(routes.FileExerciseController.index(toolMain.urlPart))
        }
      }
  }

  def uploadSolution(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser(parse.multipartFormData) { user =>
    implicit request =>
      ToolList.getFileToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => request.body.file(SpreadConsts.FILE_NAME) match {
          case None       => Future(BadRequest("There has been an error uploading your file!"))
          case Some(file) => toolMain.correctAndRender(user, id, file, fileExtension) map {
            case Success(render) => Ok(render)
            case Failure(error)  => BadRequest(error.getMessage)
          }
        }
      }
  }

  def downloadTemplate(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { _ =>
    implicit request =>
      ToolList.getFileToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.futureCompleteExById(id) map {
          case Some(exercise) => Ok.sendFile(exercise.templateFilePath(toolMain, fileExtension).toFile)
          case None           => Redirect(routes.FileExerciseController.index(toolMain.urlPart))
        }
      }
  }

  def downloadCorrected(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getFileToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.futureCompleteExById(id) map {
          case Some(exercise) =>
            val correctedFilePath = toolMain.solutionDirForExercise(user.username, exercise.ex.id) / (exercise.templateFilename + SpreadConsts.CORRECTION_ADD_STRING + "." + fileExtension)
            Ok.sendFile(correctedFilePath.toFile)
          case None           => BadRequest("There is no such exercise!")
        }
      }
  }

}