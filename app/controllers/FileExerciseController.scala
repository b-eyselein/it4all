package controllers

import better.files.File
import javax.inject.{Inject, Singleton}
import model.core._
import model.spread.SpreadConsts
import model.toolMains.{FileExerciseToolMain, ToolList}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class FileExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)
                                      (implicit ec: ExecutionContext) extends ASingleExerciseController(cc, dbcp, tl) {

  // Abstract types

  override type ToolMainType = FileExerciseToolMain

  override protected def getToolMain(toolType: String): Option[FileExerciseToolMain] = toolList.getFileToolMainOption(toolType)

  // Routes

  def exercise(toolType: String, id: Int, fileExtension: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) map {
        case Some(exercise) => Ok(toolMain.renderExercise(user, exercise, fileExtension))
        case None           => Redirect(routes.MainExerciseController.index(toolMain.urlPart))
      }
  }

  def uploadSolution(toolType: String, id: Int, fileExtension: String): EssentialAction = futureWithUserWithBodyParser(parse.multipartFormData) { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$toolType<<"))
        case Some(toolMain) =>
          request.body.file(SpreadConsts.FILE_NAME) match {
            case None       => Future(BadRequest("There has been an error uploading your file!"))
            case Some(file) => toolMain.correctAndRender(user, id, file, fileExtension) map {
              case Success(render) => Ok(render)
              case Failure(error)  => BadRequest(error.getMessage)
            }
          }
      }
  }

  def downloadTemplate(toolType: String, id: Int, fileExtension: String): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) map {
        case Some(exercise) =>
          val fileToSend: File = toolMain.templateDirForExercise(exercise.ex.id) / (exercise.templateFilename + "." + fileExtension)
          Ok.sendFile(fileToSend.toJava)
        case None           => Redirect(routes.MainExerciseController.index(toolMain.urlPart))
      }
  }

  def downloadCorrected(toolType: String, id: Int, fileExtension: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) map {
        case Some(exercise) =>
          val correctedFilePath = toolMain.solutionDirForExercise(user.username, exercise.ex.id) / (exercise.templateFilename + SpreadConsts.CORRECTION_ADD_STRING + "." + fileExtension)
          Ok.sendFile(correctedFilePath.toJava)
        case None           => BadRequest("There is no such exercise!")
      }
  }

}