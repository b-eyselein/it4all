package controllers.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Answer;
import model.AnswerKey;
import model.Correctness;
import model.Question;
import model.QuestionRating;
import model.QuestionRatingKey;
import model.QuestionResult;
import model.QuestionType;
import model.QuestionUser;
import model.Quiz;
import model.Util;
import model.user.Role;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class QuestionController extends ExerciseController {

  private static final String ATTEMPT_FIELD = "attempt";
  private static final String QUIZ_ID_FIELD = "quizId";

  @Inject
  public QuestionController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public static User getUser() {
    User user = ExerciseController.getUser();

    if(QuestionUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new QuestionUser(user.name).save();

    return user;
  }

  private static void saveRating(String name, int id, int stars) {
    QuestionRatingKey key = new QuestionRatingKey(id, name);
    QuestionRating rating = QuestionRating.finder.byId(key);

    if(rating == null)
      rating = new QuestionRating(key);

    rating.rating = stars;
    rating.save();
  }

  public Result correct(int id) {
    User user = getUser();

    Question question = Question.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();

    List<Answer> selectedAnswers = readSelAnswers(question, form);
    QuestionResult result = new QuestionResult(selectedAnswers, question);
    
    return ok(views.html.questionResult.render(user, result));
  }

  public Result editQuestion(int id) {
    DynamicForm form = factory.form().bindFromRequest();

    Question question = readQuestionFromForm(form, Question.finder.byId(id));
    question.save();
    for(Answer answer: question.answers)
      answer.save();

    return ok(views.html.questionadmin.choiceCreation.render(getUser(), Arrays.asList(question)));
  }

  public Result editQuestionForm(int id) {
    User user = getUser();
    Question question = Question.finder.byId(id);

    if(question.author.equals(user.name) || user.role == Role.ADMIN)
      return ok(views.html.editQuestionForm.render(user, question, true));

    return redirect(routes.QuestionController.index());
  }

  public Result index() {
    return ok(views.html.choiceoverview.render(getUser(), Quiz.finder.all()));
  }

  public Result newQuestion() {
    DynamicForm form = factory.form().bindFromRequest();

    int id = findMinimalNotUsedId(Question.finder);
    // TODO: evtl. suche nach ähnlichem Text?
    
    Question question = readQuestionFromForm(form, new Question(id));
    question.save();
    for(Answer answer: question.answers)
      answer.save();

    return ok(views.html.questionadmin.choiceCreation.render(getUser(), Arrays.asList(question)));
  }

  public Result newQuestionForm() {
    return ok(views.html.newQuestionForm.render(getUser(), null, false));
  }

  public Result question(int id) {
    Question question = Question.finder.byId(id);

    if(question == null)
      return redirect(controllers.choice.routes.QuestionController.index());

    switch(question.questionType) {
    case MULTIPLE:
    case SINGLE:
      return ok(views.html.choiceQuestion.render(getUser(), question));
    case FILLOUT_WITH_ORDER:
    case FILLOUT_WITHOUT_ORDER:
      return ok(views.html.filloutquestion.render(getUser(), question));
    default:
      return redirect(routes.QuestionController.index());
    }
  }

  public Result questions() {
    return ok(views.html.questions.render(getUser(), Question.finder.all()));
  }

  public Result quiz(int id) {
    return ok(views.html.quiz.render(getUser(), Quiz.finder.byId(id)));
  }

  public Result quizCorrection(int quizId, int questionId) {
    User user = getUser();

    Quiz quiz = Quiz.finder.byId(quizId);

    Question question = quiz.questions.get(questionId - 1);
    DynamicForm form = factory.form().bindFromRequest();

    List<Answer> selectedAnswers = readSelAnswers(question, form);
    QuestionResult result = new QuestionResult(selectedAnswers, question);

    int stars = Integer.parseInt(form.get("stars"));
    if(stars != -1)
      saveRating(user.name, question.id, stars);

    return ok(views.html.quizQuestionResult.render(user, quiz, result));
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

    return redirect(routes.QuestionController.quizQuestion(quizId, 1));
  }

  public Result quizzes() {
    return ok(views.html.quizzes.render(getUser(), Quiz.finder.all()));
  }

  private Question readQuestionFromForm(DynamicForm form, Question question) {
    question.title = form.get("title");
    question.text = form.get("text");
    question.author = getUser().name;
    question.questionType = QuestionType.valueOf(form.get("type"));

    int numOfAnswers = Integer.parseInt(form.get("numOfAnswers"));
    question.answers = new ArrayList<>(numOfAnswers);

    // FIXME: Punktzahl...!

    IntStream.range(1, numOfAnswers + 1).forEach(count -> {
      AnswerKey key = new AnswerKey(question.id, count);
      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);
      answer.correctness = Correctness.valueOf(form.get("correctness_" + count));
      answer.text = form.get(Integer.toString(count));

      question.answers.add(answer);
    });
    return question;
  }

  private List<Answer> readSelAnswers(Question question, DynamicForm form) {
    switch(question.questionType) {
    case MULTIPLE:
    // @formatter:off
    return question.answers.stream()
        .filter(ans -> form.get(Integer.toString(ans.key.id)) != null)
        .collect(Collectors.toList());
    //@formatter:on
    case SINGLE:
      return new ArrayList<>(Arrays.asList(question.getAnswer(Integer.parseInt(form.get("ans")))));
    case FILLOUT_WITH_ORDER:
    case FILLOUT_WITHOUT_ORDER:
    default:
      return Collections.emptyList();
    }
  }

}
