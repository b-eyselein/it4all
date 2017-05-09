package controllers.questions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.QuestionResult;
import model.QuestionUser;
import model.Quiz;
import model.Util;
import model.question.Answer;
import model.question.AnswerKey;
import model.question.Correctness;
import model.question.Question;
import model.question.QuestionType;
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

  private static List<Answer> readAnswersFromForm(DynamicForm form, Question question) {
    return IntStream.range(0, Question.MAX_ANSWERS).mapToObj(id -> {
      AnswerKey key = new AnswerKey(question.id, id);

      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);

      answer.text = form.get(String.valueOf(id));
      if(question.questionType == QuestionType.CHOICE)
        answer.correctness = Correctness.valueOf(form.get("correctness_" + id));
      else
        answer.correctness = Correctness.CORRECT;

      return answer;
    }).filter(ans -> !ans.text.isEmpty()).collect(Collectors.toList());
  }

  private static List<Answer> readSelAnswers(Question question, DynamicForm form) {
    // switch(question.questionType) {
    // case MULTIPLE:
//    // @formatter:off
//    return question.answers.stream()
//        .filter(ans -> form.get(Integer.toString(ans.key.id)) != null)
//        .collect(Collectors.toList());
//    //@formatter:on
    // case SINGLE:
    // return new
    // ArrayList<>(Arrays.asList(question.getAnswer(Integer.parseInt(form.get("ans")))));
    // case FILLOUT_WITH_ORDER:
    // case FILLOUT_WITHOUT_ORDER:
    // default:
    return Collections.emptyList();
    // }
  }

  public Result editQuestion(int id) {
    DynamicForm form = factory.form().bindFromRequest();

    Question question = null;
    // readQuestionFromForm(form, Question.finder.byId(id));
    // question.save();
    // for(Answer answer: question.answers)
    // answer.save();

    return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
  }

  public Result editQuestionForm(int id) {
    User user = getUser();
    Question question = Question.finder.byId(id);

    if(question.author.equals(user.name) || user.role == Role.ADMIN)
      return ok(views.html.editQuestionForm.render(user, question, true));

    return redirect(routes.QuestionController.index());
  }

  public Result index() {
    return ok(views.html.questionIndex.render(getUser(), Quiz.finder.all()));
  }

  public Result newQuestion(String type) {
    DynamicForm form = factory.form().bindFromRequest();

    String title = form.get("title");

    Question question = Question.finder.where().eq("title", title).findUnique();
    if(question == null)
      question = new Question(findMinimalNotUsedId(Question.finder));

    question.title = title;
    question.text = form.get("text");
    question.author = form.get("author");
    question.questionType = QuestionType.valueOf(type);
    if(question.questionType != QuestionType.FREETEXT)
      question.answers = readAnswersFromForm(form, question);

    question.save();
    question.answers.forEach(Answer::save);

    return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
  }

  public Result newQuestionForm(String type) {
    return ok(views.html.question.newQuestionForm.render(getUser(), QuestionType.valueOf(type)));
  }

  public Result question(int id) {
    Question question = Question.finder.byId(id);

    if(question == null)
      return redirect(controllers.questions.routes.QuestionController.index());

    return ok(views.html.question.question.render(getUser(), question));
  }

  public Result questionResult(int id) {
    User user = getUser();

    Question question = Question.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();

    List<Answer> selectedAnswers = readSelAnswers(question, form);
    QuestionResult result = new QuestionResult(selectedAnswers, question);

    return ok(views.html.questionResult.render(user, result));
  }

  public Result questions() {
    return ok(views.html.questionList.render(getUser(), Question.finder.all()));
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

    return redirect(controllers.questions.routes.QuestionController.quizQuestion(quizId, 1));
  }

  public Result quizzes() {
    return ok(views.html.quizzes.render(getUser(), Quiz.finder.all()));
  }

}
