package controllers;

import controllers.core.BaseController;
import model.blanks.BlanksExercise;
import model.result.SuccessType;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Security.Authenticated(model.Secured.class) public class BlanksTestController extends BaseController {

  private static final BlanksExercise exercise = new BlanksExercise(1);

  @Inject public BlanksTestController(FormFactory f) {
    super(f);
  }

  public Result correctBlanks(int id) {
    DynamicForm form = factory().form().bindFromRequest();
    int inputCount = Integer.parseInt(form.get("count"));

    List<String> inputs = new ArrayList<>(inputCount);
    for(int count = 0; count < inputCount; count++)
      inputs.add(form.get("inp" + count));

    List<SuccessType> results = exercise.correct(inputs);

    return ok(Json.toJson(results));
  }

  public Result testBlanks() {
    return ok(views.html.blanks.render(getUser(), exercise));
  }
}
