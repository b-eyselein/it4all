package controllers.core;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import model.blanks.BlanksExercise;
import model.exercise.Success;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class BlanksTestController extends AbstractController {
  
  @Inject
  public BlanksTestController(FormFactory theFactory) {
    super(theFactory, "blanks");
  }
  
  public Result correctBlanks(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    int inputCount = Integer.parseInt(form.get("count"));
    
    List<String> inputs = new ArrayList<>(inputCount);
    for(int count = 0; count < inputCount; count++)
      inputs.add(form.get("inp" + count));
    
    BlanksExercise exercise = new BlanksExercise(id);
    List<Success> results = exercise.correct(inputs);
    
    return ok(Json.toJson(results));
  }
  
  public Result testBlanks() {
    return ok(views.html.blanks.render(getUser(), new BlanksExercise(1)));
  }
}
