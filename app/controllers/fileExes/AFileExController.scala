package controllers.fileExes

import java.nio.file.{Files, Path}
import java.sql.SQLSyntaxErrorException

import controllers.{BaseExerciseController, Secured}
import model.spread.SpreadConsts._
import model.core._
import model.core.tools.ExToolObject
import model._
import model.spread.SpreadConsts
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Call, ControllerComponents, EssentialAction, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait FileExToolObject extends ExToolObject {

  override type CompEx <: FileCompleteEx[_]

  val fileTypes: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] =
    fileTypes filter (ft => exercise.available(ft._1)) map (ft => (exerciseRoute(exercise.ex, ft._1), s"Mit ${ft._2} bearbeiten"))

  def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call

  def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call

}

abstract class AFileExController[E <: Exercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: FileExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured with FileUtils {

  override type CompEx <: FileCompleteEx[E]

  override def saveAndPreviewExercises(admin: User, read: Seq[CompEx]): Future[Result] =
    saveRead(read) map (_ => Ok(previewExercises(admin, read))) recover {
      // FIXME: Failures!
      case sqlError: SQLSyntaxErrorException =>
        sqlError.printStackTrace()
        BadRequest(sqlError.getMessage)
      case throwable                         =>
        println("\nERROR: ")
        throwable.printStackTrace()
        BadRequest(throwable.getMessage)
    }


  override protected def saveRead(read: Seq[CompEx]): Future[Seq[Int]] = Future.sequence(read map { ex =>
    val pathTries = checkFiles(ex)
    pathTries.foreach(println)
    saveReadToDb(ex)
  })

  protected def saveReadToDb(read: CompEx): Future[Int]

  protected def checkFiles(ex: CompEx): List[Try[Path]]

  def exercise(id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExById(id) flatMap {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          renderExercise(user, exercise, fileExtension) map (Ok(_))
        case None           => Future(Redirect(toolObject.indexCall))
      }
  }

  def uploadSolution(id: Int, fileExtension: String): EssentialAction = futureWithUser(parse.multipartFormData) { user =>
    implicit request =>
      request.body.file(SpreadConsts.FILE_NAME) map { file =>
        // TODO: user file...
        Future(Ok("TODO!"))
      } getOrElse Future(BadRequest("There has been an error uploading your file!"))
    //      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
    //        solution => completeExById(id) map {
    //          case None => BadRequest("TODO!")
    //
    //          case Some(ex) => correctEx(user, solution, ex, part) match {
    //            case Success(correctionResult) =>
    //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
    //              Ok(renderCorrectionResult(user, correctionResult))
    //
    //            case Failure(error) =>
    //              BadRequest(views.html.main.render("Fehler", user, new Html(""), new Html(
    //                s"""<pre>${error.getMessage}:
    //                   |${error.getStackTrace mkString "\n"}</pre>""".stripMargin)))
    //          }
    //        })
  }

  def downloadTemplate(id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExById(id) map {
        case Some(exercise) =>
          val templateFilePath = exercise.templateFilePath(fileExtension)
          println(templateFilePath.toAbsolutePath)
          Ok.sendFile(templateFilePath.toFile, inline = true)
        case None           => Redirect(toolObject.indexCall)
      }
  }

  def downloadCorrected(id: Int, fileExtension: String): EssentialAction = withUser { user =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None           => BadRequest("This exercise does not exist!")
      //        case Some(exercise) =>
      //          val fileToDownload = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, fileExtension)
      //
      //          if (fileToDownload.toFile.exists) Ok.sendFile(fileToDownload.toFile)
      //          else Redirect(routes.SpreadController.index(0))
      //      }
      Ok("TODO")
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def correctEx(user: User, solution: PathSolution, exercise: CompEx, part: String): Try[CompleteResult[R]] = ???

  protected def renderExercise(user: User, exercise: CompEx, part: String): Future[Html] = ???

  protected def renderResult(correctionResult: CompleteResult[R]): Html = ???

}
