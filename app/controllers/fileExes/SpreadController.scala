package controllers.fileExes

import java.nio.file.Path
import javax.inject._

import controllers.Secured
import controllers.fileExes.SpreadController._
import model.User
import model.core._
import model.spread._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object SpreadController {

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

}

@Singleton
class SpreadController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AFileExController[SpreadExercise, SpreadSheetCorrectionResult](cc, dbcp, r, SpreadToolObject) with Secured {

  // Yaml

  override type CompEx = SpreadExercise

  override implicit val yamlFormat: YamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // db

  import profile.api._

  override type TQ = repo.SpreadExerciseTable

  override def tq = repo.spreadExercises

  override def completeExes: Future[Seq[SpreadExercise]] = db.run(repo.spreadExercises.result)

  override def completeExById(id: Int): Future[Option[SpreadExercise]] = db.run(repo.spreadExercises.filter(_.id === id).result.headOption)

  override def saveReadToDb(read: SpreadExercise): Future[Int] = db.run(repo.spreadExercises insertOrUpdate read)

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