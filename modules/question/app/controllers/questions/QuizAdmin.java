
package controllers.questions;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseCollectionAdminController;
import model.question.Question;
import model.quiz.Quiz;
import model.quiz.QuizReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class QuizAdmin extends AExerciseCollectionAdminController<Question, Quiz> {

  @Inject
  public QuizAdmin(FormFactory theFactory) {
    super(theFactory, QuestionRoutesObject$.MODULE$, Question.finder, Quiz.finder, new QuizReader());
  }

  @Override
  public Result adminIndex() {
    // TODO Auto-generated method stub
    return null;
  }

  // @Override
  // public Result newExercise() {
  // DynamicForm form = factory.form().bindFromRequest();
  //
  // int id = findMinimalNotUsedId(Quiz.finder);
  // String title = form.get(StringConsts.TITLE_NAME);
  // String theme = form.get(StringConsts.THEME_NAME);
  //
  // Quiz quiz = finder.byId(id);
  // if(quiz == null)
  // quiz = new Quiz(id, title, theme);
  // else
  // return badRequest("TODO!");
  //
  // quiz.save();
  //
  // return ok(views.html.questionAdmin.quizCreated.render(getUser(), quiz));
  // }

  // @Override
  // public Result newExerciseCollectionForm() {
  // return ok(views.html.questionAdmin.newQuizForm.render(getUser()));
  // }
  //
  // @Override
  // public Result newExerciseForm() {
  // return ok(views.html.questionAdmin.newQuizForm.render(getUser()));
  // }
  //
  // @Override
  // public Html renderCollectionCreated(List<Quiz> created) {
  // // TODO Auto-generated method stub
  // return null;
  // }

  // @Override
  // public Html renderCreated(List<Quiz> created) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // protected Html renderCreated(List<Question> created) {
  // // TODO Auto-generated method stub
  // return null;
  // }
  //
  // @Override
  // protected Html renderCreated(List<Question> created) {
  // // TODO Auto-generated method stub
  // return null;
  // }

  @Override
  public Html renderCollectionCreated(List<Quiz> created) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExCollCreationForm(User user, Quiz quiz) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExEditForm(User user, Question exercise, boolean isCreation) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExerciseCollections(User user, List<Quiz> allCollections) {
    // TODO Auto-generated method stub
    return null;
  }

}
