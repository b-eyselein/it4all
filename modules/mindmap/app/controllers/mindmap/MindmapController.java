package controllers.mindmap;

import controllers.core.BaseController;
import model.Secured;
import model.mindmap.Evaluation;
import model.mindmap.Validation;
import model.mindmap.evaluation.ParsingException;
import model.mindmap.evaluation.enums.EvalParserType;
import play.api.Configuration;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Security.Authenticated(Secured.class) public class MindmapController extends BaseController {

  private static final Path BASE_PATH = Paths.get("conf", "resources", "mindmap");

  private static final Path SOLUTION_PATH = Paths.get(BASE_PATH.toString(), "solution.xml");

  private static final Path INPUT_PATH = Paths.get(BASE_PATH.toString(), "input.xml");

  private static final Path META_PATH = Paths.get(BASE_PATH.toString(), "meta.xls");

  private static final Path RESULT_PATH = Paths.get(BASE_PATH.toString(), "result.xls");

  private static final Path TEMPLATE_PATH = Paths.get(BASE_PATH.toString(), "template.mmas");

  private static final Path ALT_INPUT_PATH = Paths.get(BASE_PATH.toString(), "als_input.xml");

  private static final Path ALT_SOLUTION_PATH = Paths.get(BASE_PATH.toString(), "alt_solution.xml");

  @Inject public MindmapController(Configuration c, FormFactory f) {
    super(c, f);
  }

  public Result index() {
    return ok(views.html.mindmapindex.render(getUser()));
  }

  public Result upload() {
    // TODO: getFile, correct and present for download!
    try {
      Validation.validateMindMap(SOLUTION_PATH);
      Validation.validateMindMap(INPUT_PATH);

      // if false: stop here and let admin edit the meta file
      // afterwards continue here or start again
      Validation.checkForMeta(SOLUTION_PATH, META_PATH);
      Validation.validateMeta(META_PATH);

      System.out.println("Validation done.");

      Evaluation.evaluate(EvalParserType.MINDMANAGER.getEvalParser(), INPUT_PATH, SOLUTION_PATH, RESULT_PATH,
          ALT_SOLUTION_PATH, ALT_INPUT_PATH, META_PATH, TEMPLATE_PATH);

      System.out.println("Evaluation done.");

    } catch (ParsingException | IOException e) {
      e.printStackTrace();
    }

    return ok(views.html.mindmapcorrect.render(getUser()));
  }

}
