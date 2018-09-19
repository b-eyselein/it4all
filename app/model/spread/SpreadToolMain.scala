package model.spread

import java.nio.file.Path

import javax.inject._
import model.spread.SpreadConsts.{difficultyName, durationName}
import model.spread.SpreadToolMain._
import model.toolMains.{FileExerciseToolMain, ToolState}
import model._
import play.api.data.Form
import play.api.data.Forms._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object SpreadToolMain {

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

}

@Singleton
class SpreadToolMain @Inject()(override val tables: SpreadTableDefs)(implicit ec: ExecutionContext)
  extends FileExerciseToolMain("Spread", "spread") {

  // Abstract types

  override type ExType = SpreadExercise

  override type CompExType = SpreadExercise

  override type Tables = SpreadTableDefs

  override type R = SpreadSheetCorrectionResult

  override type PartType = SpreadExPart

  override type ReviewType = SpreadExerciseReview

  // Other members

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = SpreadConsts

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  override val exParts: Seq[SpreadExPart] = SpreadExParts.values

  // Forms

  // TODO: create Form mapping ...
  override val compExForm: Form[SpreadExercise] = null

  override def exerciseReviewForm(username: String, completeExercise: SpreadExercise, exercisePart: SpreadExPart): Form[SpreadExerciseReview] = {

    val apply = (diffStr: String, dur: Option[Int]) =>
      SpreadExerciseReview(username, completeExercise.id, completeExercise.semanticVersion, exercisePart, Difficulties.withNameInsensitive(diffStr), dur)

    val unapply = (cr: SpreadExerciseReview) => Some((cr.difficulty.entryName, cr.maybeDuration))

    Form(
      mapping(
        difficultyName -> nonEmptyText,
        durationName -> optional(number(min = 0, max = 100))
      )(apply)(unapply)
    )
  }

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

  override def instantiateExercise(id: Int, state: ExerciseState): SpreadExercise =
    SpreadExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, sampleFilename = "", templateFilename = "")

  // Views

  override def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.fileExercises.spread.spreadExercise(user, exercise.ex, (part, fileTypes(part)))

  override def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.fileExercises.spread.spreadCorrectionResult(user, correctionResult, exercise, fileExtension)

  override def renderEditRest(exercise: SpreadExercise): Html = ???

  // Correction

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath, comparePath = learnerFilePath, conditionalFormating = false, compareCharts = false)
    }

}