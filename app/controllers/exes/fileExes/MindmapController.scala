package controllers.exes.fileExes

import java.nio.file.Path
import javax.inject._

import controllers.Secured
import model.User
import model.core._
import model.mindmap.MindmapExercise
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AFileExController[MindmapExercise, EvaluationResult, GenericCompleteResult[EvaluationResult]](cc, dbcp, r, MindMapToolObject) with Secured {

  // Reading solution from requests

  override type SolType = this.type

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[MindmapController.this.type] = ???

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[MindmapController.this.type] = ???

  // Yaml

  override type CompEx = MindmapExercise

  override implicit val yamlFormat: YamlFormat[MindmapExercise] = null

  // db

  override type TQ = repo.MindmapExercisesTable

  override def tq = repo.mindmapExercises

  override def saveReadToDb(compEx: MindmapExercise): Future[Int] = ???

  override protected def checkFiles(ex: MindmapExercise): List[Try[Path]] = ???

  protected def futureCompleteExById(id: Int): Future[Option[MindmapExercise]] = ???

  protected def futureCompleteExes: Future[Seq[MindmapExercise]] = ???

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

}
