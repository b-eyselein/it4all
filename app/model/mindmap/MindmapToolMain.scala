package model.mindmap

import java.nio.file.Path

import javax.inject._
import model.core._
import model.toolMains.FileExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, User}
import play.api.data.Form
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton
class MindmapToolMain @Inject()(override val tables: MindmapTableDefs)(implicit ec: ExecutionContext) extends FileExerciseToolMain("mindmap") {

  // Abstract types

  override type ExType = MindmapExercise

  override type CompExType = MindmapExercise

  override type Tables = MindmapTableDefs

  override type R = EvaluationResult

  override type PartType = MindmapExPart

  // Other members

  override val toolname: String = "Mindmap"

  override val consts: Consts = MindmapConsts

  override val fileTypes: Map[String, String] = Map.empty

  override val exParts: Seq[PartType] = MindmapExParts.values

  override implicit val compExForm: Form[MindmapExercise] = null

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[MindmapExercise] = null

  // File checking

  override def checkFiles(ex: MindmapExercise): Seq[Try[Path]] = ???

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): MindmapExercise =
    MindmapExercise(id, title = "", author = "", text = "", state)

  // Views

  override def renderExercise(user: User, exercise: MindmapExercise, part: String): Html = ???

  override def renderResult(user: User, correctionResult: EvaluationResult, exercise: MindmapExercise, fileExtension: String): Html = views.html.mindmap.mindmapcorrect.render(user)

  override def renderEditRest(exercise: MindmapExercise): Html = ???

  // Correction

  override protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): EvaluationResult = ???

  //        Validation.validateMindMap(SOLUTION_PATH)
  //        Validation.validateMindMap(INPUT_PATH)
  //
  //         if false: stop here and let admin edit the meta file
  //         afterwards continue here or start again
  //        Validation.checkForMeta(SOLUTION_PATH, META_PATH)
  //        Validation.validateMeta(META_PATH)
  //
  //        Evaluation.evaluate(EvalParserType.MINDMANAGER.getEvalParser, INPUT_PATH, SOLUTION_PATH, RESULT_PATH,
  //          ALT_SOLUTION_PATH, ALT_INPUT_PATH, META_PATH, TEMPLATE_PATH)
  //
  //      } catch {
  //        case e@(_: ParsingException | _: IOException) => e.printStackTrace()
  //      }

}
