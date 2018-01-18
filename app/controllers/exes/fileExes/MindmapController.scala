package controllers.exes.fileExes

import java.nio.file.Path
import javax.inject._

import controllers.Secured
import model.User
import model.core._
import model.mindmap.{MindmapExercise, MindmapTableDefs}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{AnyContent, ControllerComponents, Request, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: MindmapTableDefs)(implicit ec: ExecutionContext)
  extends AFileExController[MindmapExercise, MindmapExercise, EvaluationResult, GenericCompleteResult[EvaluationResult], MindmapTableDefs](cc, dbcp, t, MindMapToolObject) with Secured {

  // Reading solution from requests

  override type SolType = this.type

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[MindmapController.this.type] = ???

  override def readSolutionFromPutRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[MindmapController.this.type] = ???

  // Yaml

  override implicit val yamlFormat: YamlFormat[MindmapExercise] = null

  // db

  override def saveReadToDb(compEx: MindmapExercise): Future[Int] = Future(-1) //???

  override protected def checkFiles(ex: MindmapExercise): Seq[Try[Path]] = Seq.empty // ???

  // Views

  override protected def renderExercise(user: User, exercise: MindmapExercise, part: String): Html = ???

  override protected def renderResult(user: User, correctionResult: EvaluationResult, exercise: MindmapExercise, fileExtension: String): Html = ???

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
  override protected def onSubmitCorrectionResult(user: User, result: GenericCompleteResult[EvaluationResult]): Result = ???

  override protected def onSubmitCorrectionError(user: User, error: Throwable): Result = ???

  override protected def onLiveCorrectionResult(result: GenericCompleteResult[EvaluationResult]): Result = ???

  override protected def onLiveCorrectionError(error: Throwable): Result = ???
}
