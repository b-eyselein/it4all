package controllers.exes

import java.io.IOException
import java.nio.file.{Path, Paths}
import javax.inject._

import controllers.core.AIdExController
import controllers.exes.MindmapController._
import model.User
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.{Repository, Secured, StringSolution}
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

object MindmapController {

  val BASE_PATH: Path = Paths.get("conf", "resources", "mindmap")

  val SOLUTION_PATH: Path = Paths.get(BASE_PATH.toString, "solution.xml")

  val INPUT_PATH: Path = Paths.get(BASE_PATH.toString, "input.xml")

  val META_PATH: Path = Paths.get(BASE_PATH.toString, "meta.xls")

  val RESULT_PATH: Path = Paths.get(BASE_PATH.toString, "result.xls")

  val TEMPLATE_PATH: Path = Paths.get(BASE_PATH.toString, "template.mmas")

  val ALT_INPUT_PATH: Path = Paths.get(BASE_PATH.toString, "als_input.xml")

  val ALT_SOLUTION_PATH: Path = Paths.get(BASE_PATH.toString, "alt_solution.xml")
}

class MindmapController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[MindmapExercise, EvaluationResult](cc, dbcp, r, MindMapToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // db

  override type DbType = MindmapExercise

  override implicit val yamlFormat: YamlFormat[MindmapExercise] = null

  override type TQ = repo.MindmapExercisesTable

  override def tq = repo.mindmapExercises


  override protected def correctEx(sol: StringSolution, exercise: Option[MindmapExercise], user: User): Try[CompleteResult[EvaluationResult]] = ???

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

  override protected def renderExesListRest: Html = ???

}
