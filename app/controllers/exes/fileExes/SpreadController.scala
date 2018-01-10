package controllers.exes.fileExes

import java.nio.file.Path
import javax.inject._

import controllers.Secured
import controllers.exes.fileExes.SpreadController._
import model.User
import model.core._
import model.spread._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
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
  extends AFileExController[SpreadExercise, SpreadExercise, SpreadSheetCorrectionResult, GenericCompleteResult[SpreadSheetCorrectionResult], SpreadTableDefs](cc, dbcp, t, SpreadToolObject) with Secured {

  // Reading solution from requests

  override type SolType = this.type

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SpreadController.this.type] = ???

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SpreadController.this.type] = ???

  // Yaml

  override implicit val yamlFormat: YamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // db

  import profile.api._

  override def futureCompleteExes: Future[Seq[SpreadExercise]] = db.run(tables.spreadExercises.result)

  override def futureCompleteExById(id: Int): Future[Option[SpreadExercise]] = db.run(tables.spreadExercises.filter(_.id === id).result.headOption)

  override def saveReadToDb(read: SpreadExercise): Future[Int] = db.run(tables.spreadExercises insertOrUpdate read)

  override protected def checkFiles(ex: SpreadExercise): List[Try[Path]] = SpreadToolObject.fileTypes flatMap {
    case (fileEnding, _) =>
      // FIXME: use result!
      val sampleFilename = ex.sampleFilename + "." + fileEnding
      val templateFilename = ex.templateFilename + "." + fileEnding

      List(
        copy(sampleFilename, toolObject.exerciseResourcesFolder, toolObject.sampleDirForExercise(ex)),
        copy(templateFilename, toolObject.exerciseResourcesFolder, toolObject.templateDirForExercise(ex))
      )
  } toList

  // Views

  override protected def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.spread.spreadExercise.render(user, exercise.ex, (part, SpreadToolObject.fileTypes(part)))

  override protected def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.spread.spreadCorrectionResult.render(user, correctionResult, exercise, fileExtension)

  // Correction

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath, comparePath = learnerFilePath, conditionalFormating = false, compareCharts = false)
    }

}