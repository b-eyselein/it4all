package controllers.questions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.Quiz;
import model.StringConsts;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class QuizAdmin extends AbstractAdminController<Quiz, QuizReader> {
  
  @Inject
  public QuizAdmin(FormFactory theFactory, String theExerciseType) {
    super(theFactory, Quiz.finder, theExerciseType, new QuizReader());
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public Quiz getNew(int id) {
    return new Quiz(id);
  }
  
  @Override
  public Result index() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Result newExercise() {
    DynamicForm form = factory.form().bindFromRequest();
    
    int id = findMinimalNotUsedId(Quiz.finder);
    String title = form.get(StringConsts.TITLE_NAME);
    
    Quiz quiz;
    
    // Is there another quiz with the same title?
    List<Quiz> other = Quiz.finder.all().stream().filter(q -> q.title.equals(title)).collect(Collectors.toList());
    if(!other.isEmpty())
      quiz = other.get(0);
    else
      quiz = new Quiz(id);
    
    quiz.title = title;
    quiz.theme = form.get("theme");
    quiz.text = form.get(StringConsts.TEXT_NAME);
    quiz.save();
    
    return ok(views.html.questionAdmin.quizCreated.render(getUser(), quiz));
  }
  
  @Override
  public Result newExerciseForm() {
    return ok(views.html.questionAdmin.newQuizForm.render(getUser()));
  }
  
  @Override
  public Html renderCreated(List<Quiz> created) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected void initRemainingExFromForm(DynamicForm form, Quiz exercise) {
    // TODO Auto-generated method stub
    
  }
  
}
