package controllers

import model.core._
import model.toolMains.FixedExToolMain
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Try}

abstract class AFixedExController(cc: ControllerComponents, dbcp: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils with ExerciseFormMappings {

  override protected type ToolMainType <: FixedExToolMain

  def adminImportToRead(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain: FixedExToolMain) =>
    implicit request =>
      // FIXME: refactor!!!!!!!!!

      val readTries: Seq[Try[toolMain.ReadType]] = toolMain.readImports

      val (readSuccesses: Seq[toolMain.ReadType], readFailures: Seq[Failure[toolMain.ReadType]]) = CommonUtils.splitTries(readTries)

      toolMain.futureSaveRead(readSuccesses) map { saveResults: Seq[(toolMain.ReadType, Boolean)] =>
        val readAndSaveResult = ReadAndSaveResult(saveResults map (sr => new ReadAndSaveSuccess[toolMain.ReadType](sr._1, sr._2)), readFailures)

        for (failure <- readAndSaveResult.failures) {
          Logger.error("There has been an error reading a yaml object: ", failure.exception)
        }

        Ok(toolMain.previewExercise(admin, readAndSaveResult))
      }
  }

}
