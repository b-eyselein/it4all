package controllers.exes.fileExes

import java.nio.file.Path

import controllers.exes.fileExes.SpreadController._
import javax.inject._
import model.spread._
import model.yaml.MyYamlFormat
import model.{Consts, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

object SpreadController {

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

}

@Singleton
class SpreadController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, override val tables: SpreadTableDefs)(implicit ec: ExecutionContext)
  extends AFileExToolMain[SpreadExercise, SpreadExercise] {

  override val urlPart : String = "spread"
  override val toolname: String = "Spread"
  override val exType  : String = "spread"
  override val consts  : Consts = SpreadConsts

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  // Abstract types

  override type Tables = SpreadTableDefs

  override type R = SpreadSheetCorrectionResult

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // db

  //  protected def checkFiles(ex: SpreadExercise): List[Try[Path]] = SpreadToolObject.fileTypes flatMap {
  //    case (fileEnding, _) =>
  //  FIXME: use result !
  //      val sampleFilename = ex.sampleFilename + "." + fileEnding
  //      val templateFilename = ex.templateFilename + "." + fileEnding
  //
  //      List(
  //        copy(sampleFilename, toolObject.exerciseResourcesFolder, toolObject.sampleDirForExercise(ex.id)),
  //        copy(templateFilename, toolObject.exerciseResourcesFolder, toolObject.templateDirForExercise(ex.id))
  //      )
  //  } toList

  // Views

  override def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.spread.spreadExercise.render(user, exercise.ex, (part, fileTypes(part)))

  override def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.spread.spreadCorrectionResult.render(user, correctionResult, exercise, fileExtension)

  // Correction

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath, comparePath = learnerFilePath, conditionalFormating = false, compareCharts = false)
    }

}