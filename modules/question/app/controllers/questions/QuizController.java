package controllers.questions;

import javax.inject.Inject;

import controllers.core.ExerciseCollectionController;
import model.question.Question;
import model.quiz.Quiz;
import play.data.FormFactory;
import play.mvc.Result;

public class QuizController extends ExerciseCollectionController<Question, Quiz> {

  private static final String ATTEMPT_FIELD = "attempt";

  private static final String QUIZ_ID_FIELD = "quizId";

  @Inject
  public QuizController(FormFactory theFactory) {
    super(theFactory, "question", Quiz.finder);
  }

  public Result quiz(int id) {
    return ok(views.html.quiz.render(getUser(), Quiz.finder.byId(id)));
  }

  public Result quizCorrection(int quizId, int questionId) {
    // User user = getUser();
    //
    // Quiz quiz = Quiz.finder.byId(quizId);
    //
    // Question question = quiz.questions.get(questionId - 1);
    // DynamicForm form = factory.form().bindFromRequest();
    //
    // List<Answer> selectedAnswers = readSelAnswers(question, form);
    // QuestionResult result = new QuestionResult(selectedAnswers, question);
    //
    // return ok(views.html.quizQuestionResult.render(user, quiz, result));
    return ok("TODO!");
  }

  public Result quizQuestion(int quizId, int questionId) {
    return ok(views.html.quizQuestion.render(getUser(), Quiz.finder.byId(quizId), questionId - 1));
  }

  public Result quizStart(int quizId) {
    // TODO: from db...
    int attempt = 1;

    // TODO: Initialize session
    session().put(ATTEMPT_FIELD, Integer.toString(attempt));
    session().put(QUIZ_ID_FIELD, Integer.toString(quizId));

    return redirect(controllers.questions.routes.QuizController.quizQuestion(quizId, 1));
  }

  public Result quizzes() {
    return ok(views.html.quizzes.render(getUser(), Quiz.finder.all()));
  }

}
