package controllers.exes.fileExes

import java.nio.file.Path

import javax.inject._
import model.core._
import model.mindmap.{MindmapConsts, MindmapExercise, MindmapTableDefs}
import model.yaml.MyYamlFormat
import model.{Consts, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@Singleton
class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, override val tables: MindmapTableDefs)(implicit ec: ExecutionContext)
  extends AFileExToolMain[MindmapExercise, MindmapExercise] {

  override val urlPart : String = "mindmap"
  override val toolname: String = "Mindmap"
  override val exType  : String = "mindmap"
  override val consts  : Consts = MindmapConsts

  override val fileTypes: Map[String, String] = Map.empty

  // Abstract types

  override type Tables = MindmapTableDefs

  override type R = EvaluationResult

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[MindmapExercise] = null


  // Views

  override def renderExercise(user: User, exercise: MindmapExercise, part: String): Html = ???

  override def renderResult(user: User, correctionResult: EvaluationResult, exercise: MindmapExercise, fileExtension: String): Html = ???

  //Ok(views.html.mindmap.mindmapcorrect.render(user))

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
  //        System.out.println("Validation done.")
  //
  //        Evaluation.evaluate(EvalParserType.MINDMANAGER.getEvalParser, INPUT_PATH, SOLUTION_PATH, RESULT_PATH,
  //          ALT_SOLUTION_PATH, ALT_INPUT_PATH, META_PATH, TEMPLATE_PATH)
  //
  //        System.out.println("Evaluation done.")
  //
  //      } catch {
  //        case e@(_: ParsingException | _: IOException) => e.printStackTrace()
  //      }
}
