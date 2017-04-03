package controllers.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.ChoiceAnswer;
import model.ChoiceQuestion;
import model.ChoiceQuestionKey;
import model.ChoiceResult;
import model.Util;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class ChoiceController extends ExerciseController {
  
  @Inject
  public ChoiceController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result correct(int quizId, int id) {
    ChoiceQuestionKey key = new ChoiceQuestionKey(id, quizId);
    ChoiceQuestion question = ChoiceQuestion.finder.byId(key);
    DynamicForm form = factory.form().bindFromRequest();
    
    List<ChoiceAnswer> selectedAnswers = readSelAnswers(question, form);
    ChoiceResult result = new ChoiceResult(selectedAnswers, question);
    
    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcresult.render(getUser(), result));
    case SINGLE:
      return ok(views.html.scresult.render(getUser(), result));
    case FILLOUT:
      return ok(views.html.filloutresult.render(getUser(), result));
    default:
      return redirect(routes.ChoiceController.index());
    }
  }
  
  public Result index() {
    return ok(views.html.choiceoverview.render(getUser(), ChoiceQuestion.finder.all()));
  }
  
  public Result question(int quizId, int id) {
    ChoiceQuestionKey key = new ChoiceQuestionKey(id, quizId);
    ChoiceQuestion question = ChoiceQuestion.finder.byId(key);
    
    if(question == null)
      return redirect(controllers.choice.routes.ChoiceController.index());
    
    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcquestion.render(getUser(), question));
    case SINGLE:
      return ok(views.html.scquestion.render(getUser(), question));
    case FILLOUT:
      return ok(views.html.filloutquestion.render(getUser(), question));
    default:
      return redirect(routes.ChoiceController.index());
    }
  }
  
  private List<ChoiceAnswer> readSelAnswers(ChoiceQuestion question, DynamicForm form) {
    switch(question.questionType) {
    case MULTIPLE:
    // @formatter:off
    return question.answers.stream()
        .filter(ans -> form.get(ans.getId() + "") != null)
        .collect(Collectors.toList());
    //@formatter:on
    case SINGLE:
      return new ArrayList<>(Arrays.asList(question.getAnswer(Integer.parseInt(form.get("ans")))));
    case FILLOUT:
    default:
      return Collections.emptyList();
    }
  }
  
}
