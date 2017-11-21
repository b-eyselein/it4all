package controllers.fileExes

import javax.inject._

import controllers.Secured
import model.User
import model.core._
import model.mindmap.MindmapExercise
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AFileExController[MindmapExercise, EvaluationResult](cc, dbcp, r, MindMapToolObject) with Secured {

  // Yaml

  override type CompEx = MindmapExercise

  override implicit val yamlFormat: YamlFormat[MindmapExercise] = null

  // db

  override type TQ = repo.MindmapExercisesTable

  override def tq = repo.mindmapExercises

  override def saveReadToDb(compEx: MindmapExercise): Future[Int] = Future(-1)

  override protected def checkFiles(ex: MindmapExercise) = ???

  // Other routes

  //  def upload: EssentialAction = withUser { user =>
  //    implicit request =>
  //       TODO: getFile, correct and present for download!
  //      try {
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
  //
  //      Ok(views.html.mindmap.mindmapcorrect.render(user))
  //  }

  // Views

  override protected def renderExesListRest: Html = ???

  override protected def renderExercise(user: User, exercise: MindmapExercise, part: String): Future[Html] = ???

  override protected def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ???

  // Correction - probably not used...

  override protected def correctEx(user: User, solution: PathSolution, exercise: MindmapExercise, part: String): Try[CompleteResult[EvaluationResult]] = ???

}
