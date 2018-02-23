package controllers.exes.fileExes

import controllers.Secured
import controllers.exes.BaseExerciseController
import javax.inject.{Inject, Singleton}
import model.core._
import model.spread.SpreadConsts
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AFileExController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: Repository)
                                 (implicit ec: ExecutionContext) extends BaseExerciseController(cc, dbcp) with Secured with FileUtils {

  // Routes

  def exercise(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = BaseExerciseController.FileToolMains(tool)
      toolMain.renderExerciseById(user, id, fileExtension) map {
        case Some(r) => Ok(r)
        case None    => Redirect(routes.AFileExController.index(toolMain.urlPart))
      }
  }

  def uploadSolution(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser(parse.multipartFormData) { user =>
    implicit request =>
      val toolMain = BaseExerciseController.FileToolMains(tool)

      request.body.file(SpreadConsts.FILE_NAME) match {
        case None       => Future(BadRequest("There has been an error uploading your file!"))
        case Some(file) => toolMain.correctAndRender(user, id, file, fileExtension) map {
          case Success(render) => Ok(render)
          case Failure(error)  => BadRequest(error.getMessage)
        }
      }
  }

  def downloadTemplate(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { _ =>
    implicit request =>
      val toolMain = BaseExerciseController.FileToolMains(tool)

      toolMain.futureCompleteExById(id) map {
        case Some(exercise) => Ok.sendFile(exercise.templateFilePath(fileExtension).toFile)
        case None           => Redirect(routes.AFileExController.index(toolMain.urlPart))
      }
  }

  def downloadCorrected(tool: String, id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = BaseExerciseController.FileToolMains(tool)

      toolMain.futureCompleteExById(id) map {
        case Some(exercise) =>
          val correctedFilePath = toolMain.solutionDirForExercise(user.username, exercise.id) / (exercise.templateFilename + SpreadConsts.CORRECTION_ADD_STRING + "." + fileExtension)
          Ok.sendFile(correctedFilePath.toFile)
        case None           => BadRequest("There is no such exercise!")
      }
  }

}