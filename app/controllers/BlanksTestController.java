package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.BaseController;
import model.blanks.BlanksExercise;
import model.exercise.Success;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class BlanksTestController extends BaseController {
  
  private static final BlanksExercise exercise = new BlanksExercise(1, "Lückentext", "bje40dc", "TODO!");
  
  @Inject
  public BlanksTestController(FormFactory theFactory) {
    super(theFactory);
  }
  
  public Result correctBlanks(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    int inputCount = Integer.parseInt(form.get("count"));
    
    List<String> inputs = new ArrayList<>(inputCount);
    for(int count = 0; count < inputCount; count++)
      inputs.add(form.get("inp" + count));
    
    List<Success> results = exercise.correct(inputs);
    
    return ok(Json.toJson(results));
  }
  
  public Result testBlanks() {
    return ok(views.html.blanks.render(getUser(), exercise));
  }
}
