package model.spread

import better.files.File
import javax.inject._
import model._
import model.spread.SpreadConsts.{difficultyName, durationName}
import model.spread.SpreadToolMain._
import model.toolMains.{FileExerciseToolMain, ToolState}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesProvider
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}

object SpreadToolMain {

  val correctors: Map[String, SpreadCorrector] = Map[String, SpreadCorrector]("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

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

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  override protected val exParts: Seq[SpreadExPart] = SpreadExParts.values

  override protected val completeResultJsonProtocol: Null = null

  // Forms

  // TODO: create Form mapping ...
  override def compExForm: Form[SpreadExercise] = ???

  override def exerciseReviewForm(username: String, completeExercise: SpreadExercise, exercisePart: SpreadExPart): Form[SpreadExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (SpreadExerciseReview(username, completeExercise.id, completeExercise.semanticVersion, exercisePart, _, _))
    (ser => Some((ser.difficulty, ser.maybeDuration)))
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[SpreadExercise] = SpreadExYamlProtocol.SpreadExYamlFormat

  // File checking

  override protected def checkFiles(ex: SpreadExercise): Seq[File] = fileTypes flatMap {
    case (fileEnding, _) =>
      //    FIXME: use result !
      val sampleFilename = ex.sampleFilename + "." + fileEnding
      val templateFilename = ex.templateFilename + "." + fileEnding

      val sampleSourceFile: File = exerciseResourcesFolder / sampleFilename
      val templateSourceFile: File = exerciseResourcesFolder / templateFilename


      val sampleTargetFile = sampleSourceFile.copyToDirectory(sampleDirForExercise(ex.id))
      val templateTargetFile = templateSourceFile.copyToDirectory(templateDirForExercise(ex.id))

      Seq(sampleTargetFile, templateTargetFile)

  } toSeq

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): SpreadExercise =
    SpreadExercise(id, SemanticVersion(0, 1, 0), title = "", author, text = "", state, sampleFilename = "", templateFilename = "")

  // Views

  override def renderExercise(user: User, exercise: SpreadExercise, part: String): Html =
    views.html.fileExercises.spread.spreadExercise(user, exercise.ex, (part, fileTypes(part)))

  override def renderResult(user: User, correctionResult: SpreadSheetCorrectionResult, exercise: SpreadExercise, fileExtension: String): Html =
    views.html.fileExercises.spread.spreadCorrectionResult(user, correctionResult, exercise, fileExtension)

  override def renderEditRest(exercise: SpreadExercise): Html = ???

  override def renderUserExerciseEditForm(user: User, newExForm: Form[SpreadExercise], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = ???

  // Correction

  override protected def correctEx(learnerFilePath: File, sampleFilePath: File, fileExtension: String): SpreadSheetCorrectionResult =
    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(samplePath = sampleFilePath.toJava.toPath, comparePath = learnerFilePath.toJava.toPath, conditionalFormating = false, compareCharts = false)
    }

}