package model.spread

import java.nio.file.Path

import javax.inject._
import model.Enums.ToolState
import model.spread.SpreadToolMain._
import model.toolMains.FileExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, User}
import play.api.data.Form
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object SpreadToolMain {

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

}

@Singleton
class SpreadToolMain @Inject()(override val tables: SpreadTableDefs)(implicit ec: ExecutionContext) extends FileExerciseToolMain("spread") {

  // Abstract types

  override type ExType = SpreadExercise

  override type CompExType = SpreadExercise

  override type Tables = SpreadTableDefs

  override type R = SpreadSheetCorrectionResult

  override type PartType = SpreadExPart

  // Other members

  override val toolname: String = "Spread"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = SpreadConsts

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  override val exParts: Seq[SpreadExPart] = SpreadExParts.values

  override implicit val compExForm: Form[SpreadExercise] = null

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // File checking

  protected def checkFiles(ex: SpreadExercise): Seq[Try[Path]] = fileTypes flatMap {
    case (fileEnding, _) =>
      //    FIXME: use result !
      val sampleFilename = ex.sampleFilename + "." + fileEnding
      val templateFilename = ex.templateFilename + "." + fileEnding

      Seq(
        copy(sampleFilename, exerciseResourcesFolder, sampleDirForExercise(ex.id)),
        copy(templateFilename, exerciseResourcesFolder, templateDirForExercise(ex.id))
      )
  } toSeq

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): SpreadExercise =
    SpreadExercise(id, title = "", author = "", text = "", state, sampleFilename = "", templateFilename = "")

  // Views

  override def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.spread.spreadExercise(user, exercise.ex, (part, fileTypes(part)))

  override def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.spread.spreadCorrectionResult(user, correctionResult, exercise, fileExtension)

  override def renderEditRest(exercise: SpreadExercise): Html = ???

  // Correction

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath, comparePath = learnerFilePath, conditionalFormating = false, compareCharts = false)
    }

}