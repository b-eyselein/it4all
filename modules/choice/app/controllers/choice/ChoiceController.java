package controllers.choice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.ExerciseController;
import model.ChoiceQuestion;
import model.ChoiceResult;
import model.Util;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class ChoiceController extends ExerciseController {
  
  @Inject
  public ChoiceController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  private static List<Integer> parseAnswers(String selectedAnswers) {
    JsonNode answerNode = Json.parse(selectedAnswers);
    List<Integer> ret = new ArrayList<>(answerNode.size());
    
    for(JsonNode answer: answerNode)
      ret.add(answer.asInt());
    
    return ret;
  }
  
  public Result correct(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    List<Integer> selectedAnswers = parseAnswers(form.get("selected"));
    
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);
    
    ChoiceResult result = new ChoiceResult(selectedAnswers, question);
    
    return ok(Json.toJson(result));
  }
  
  public Result index() {
    return ok(views.html.choiceoverview.render(getUser(), ChoiceQuestion.finder.all()));
  }
  
  public Result question(int id) {
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);
    
    if(question == null)
      return redirect(controllers.choice.routes.ChoiceController.index());
    
    return ok(views.html.question.render(getUser(), question));
  }
}
