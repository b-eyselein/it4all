package controllers.exes.fileExes

import java.nio.file.Path

import controllers.Secured
import controllers.exes.fileExes.SpreadController._
import javax.inject._
import model.User
import model.core._
import model.spread._
import model.yaml.MyYamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object SpreadController {

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

}

@Singleton
class SpreadController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: SpreadTableDefs)(implicit ec: ExecutionContext)
  extends AFileExController[SpreadExercise, SpreadExercise, SpreadTableDefs](cc, dbcp, t, SpreadToolObject) with Secured {

  // Result types

  override type R = SpreadSheetCorrectionResult

  override type CompResult = GenericCompleteResult[SpreadSheetCorrectionResult]

  // Reading solution from requests

  override type SolType = Path

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[Path] = ???

  override def readSolutionFromPutRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[Path] = ???

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // db

  import profile.api._

  override def saveReadToDb(read: SpreadExercise): Future[Int] = db.run(tables.spreadExercises insertOrUpdate read)

  override protected def checkFiles(ex: SpreadExercise): List[Try[Path]] = SpreadToolObject.fileTypes flatMap {
    case (fileEnding, _) =>
      // FIXME: use result!
      val sampleFilename = ex.sampleFilename + "." + fileEnding
      val templateFilename = ex.templateFilename + "." + fileEnding

      List(
        copy(sampleFilename, toolObject.exerciseResourcesFolder, toolObject.sampleDirForExercise(ex.id)),
        copy(templateFilename, toolObject.exerciseResourcesFolder, toolObject.templateDirForExercise(ex.id))
      )
  } toList

  // Views

  override protected def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.spread.spreadExercise.render(user, exercise.ex, (part, SpreadToolObject.fileTypes(part)))

  override protected def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.spread.spreadCorrectionResult.render(user, correctionResult, exercise, fileExtension)

  // Correction

  override protected def correctEx(user: User, sol: Path, exercise: SpreadExercise): Future[Try[GenericCompleteResult[SpreadSheetCorrectionResult]]] = ???

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath, comparePath = learnerFilePath, conditionalFormating = false, compareCharts = false)
    }

  override protected def onSubmitCorrectionResult(user: User, result: GenericCompleteResult[SpreadSheetCorrectionResult]): Html = ???

  override protected def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override protected def onLiveCorrectionResult(result: GenericCompleteResult[SpreadSheetCorrectionResult]): JsValue = ???

}