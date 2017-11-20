package controllers.exes

import java.io.IOException
import javax.inject._

import controllers.Secured
import controllers.core.AIdExController
import model.User
import model.core._
import model.mindmap.MindmapConsts._
import model.mindmap.evaluation.ParsingException
import model.mindmap.evaluation.enums.EvalParserType
import model.mindmap.{Evaluation, MindmapExercise, Validation}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[MindmapExercise, EvaluationResult](cc, dbcp, r, MindMapToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // Yaml

  override type CompEx = MindmapExercise

  override implicit val yamlFormat: YamlFormat[MindmapExercise] = null

  // db

  override type TQ = repo.MindmapExercisesTable

  override def tq = repo.mindmapExercises

  // Other routes

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.mindmap.mindmapindex.render(user)) }

  def upload: EssentialAction = withUser { user =>
    implicit request =>
      // TODO: getFile, correct and present for download!
      try {
        Validation.validateMindMap(SOLUTION_PATH)
        Validation.validateMindMap(INPUT_PATH)

        // if false: stop here and let admin edit the meta file
        // afterwards continue here or start again
        Validation.checkForMeta(SOLUTION_PATH, META_PATH)
        Validation.validateMeta(META_PATH)

        System.out.println("Validation done.")

        Evaluation.evaluate(EvalParserType.MINDMANAGER.getEvalParser, INPUT_PATH, SOLUTION_PATH, RESULT_PATH,
          ALT_SOLUTION_PATH, ALT_INPUT_PATH, META_PATH, TEMPLATE_PATH)

        System.out.println("Evaluation done.")

      } catch {
        case e@(_: ParsingException | _: IOException) => e.printStackTrace()
      }

      Ok(views.html.mindmap.mindmapcorrect.render(user))
  }

  // Views

  override protected def renderExesListRest: Html = ???

  override protected def renderExercise(user: User, exercise: MindmapExercise): Html = ???

  override protected def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ???

  // Correction - probably not used...

  override protected def correctEx(sol: StringSolution, exercise: MindmapExercise, user: User): Try[CompleteResult[EvaluationResult]] = ???

}
