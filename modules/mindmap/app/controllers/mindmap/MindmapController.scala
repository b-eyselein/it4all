package controllers.mindmap

import java.io.IOException
import java.nio.file.{Path, Paths}
import javax.inject.Inject

import controllers.core.BaseController
import controllers.mindmap.MindmapController._
import model.mindmap.evaluation.ParsingException
import model.mindmap.evaluation.enums.EvalParserType
import model.mindmap.{Evaluation, Validation}
import play.api.mvc.ControllerComponents
import play.mvc.Security

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

@Security.Authenticated(classOf[model.Secured])
class MindmapController @Inject()(cc: ControllerComponents) extends BaseController(cc) {

  def index = Action { implicit request => Ok(views.html.mindmapindex.render(getUser))
  }

  def upload = Action { implicit request =>
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

    Ok(views.html.mindmapcorrect.render(getUser))
  }

}
