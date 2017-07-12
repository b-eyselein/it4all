package controllers.questions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.BaseExerciseController;
import controllers.core.ExerciseController;
import model.QuestionResult;
import model.QuestionUser;
import model.Quiz;
import model.StringConsts;
import model.UserAnswer;
import model.UserAnswerKey;
import model.question.Answer;
import model.question.AnswerKey;
import model.question.Correctness;
import model.question.Question;
import model.user.Role;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class QuestionController extends BaseExerciseController {
  
  private static final String ATTEMPT_FIELD = "attempt";
  private static final String QUIZ_ID_FIELD = "quizId";
  
  private static final int STEP = 10;
  
  @Inject
  public QuestionController(FormFactory theFactory) {
    super(theFactory, "question");
  }
  
  public static User getUser() {
    User user = ExerciseController.getUser();
    
    if(QuestionUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new QuestionUser(user.name).save();
    
    return user;
  }
  
  private static List<Answer> readAnswersFromForm(DynamicForm form, int questionId, boolean isChoice) {
    return IntStream.range(0, Question.MAX_ANSWERS).mapToObj(id -> {
      AnswerKey key = new AnswerKey(questionId, id);
      
      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);
      
      answer.text = form.get(String.valueOf(id));
      answer.correctness = isChoice ? Correctness.valueOf(form.get("correctness_" + id)) : Correctness.CORRECT;
      
      return answer;
    }).filter(ans -> !ans.text.isEmpty()).collect(Collectors.toList());
  }
  
  private static List<Answer> readSelAnswers(Question question, DynamicForm form) {
    return question.answers.stream().filter(ans -> form.get(Integer.toString(ans.key.id)) != null)
        .collect(Collectors.toList());
  }
  
  public Result editQuestion(int id) {
    // DynamicForm form = factory.form().bindFromRequest();
    
    Question question = null; // readQuestionFromForm(form,
                              // Question.finder.byId(id));
    // question.save();
    // for(Answer answer: question.answers)
    // answer.save();
    
    return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
  }
  
  public Result editQuestionForm(int id) {
    User user = getUser();
    Question question = Question.finder.byId(id);
    
    if(question.getAuthor().equals(user.name) || user.stdRole == Role.ADMIN)
      return ok(views.html.editQuestionForm.render(user, question, true));
    
    return redirect(routes.QuestionController.index(0));
  }
  
  public Result index(int start) {
    List<Question> all = Question.finder.all();
    List<Question> questions = all.subList(Math.min(all.size(), start), Math.min(all.size(), start + STEP));
    return ok(views.html.questionIndex.render(getUser(), questions, Quiz.finder.all()));
  }
  
  public Result newQuestion(boolean isFreetext) {
    DynamicForm form = factory.form().bindFromRequest();
    
    int id = findMinimalNotUsedId(Question.finder);
    String title = form.get(StringConsts.TITLE_NAME);
    String author = form.get(StringConsts.AUTHOR_NAME);
    String text = form.get(StringConsts.TEXT_NAME);
    String maxP = form.get(StringConsts.MAX_POINTS);
    int maxPoints = maxP == null ? 0 : Integer.parseInt(maxP);
    Question.QType exerciseType = Question.QType.valueOf(form.get(StringConsts.EXERCISE_TYPE));
    
    boolean isChoice = false;
    List<Answer> answers = Collections.emptyList();
    if(!isFreetext) {
      isChoice = true; // TODO!
      answers = readAnswersFromForm(form, id, isChoice);
    }
    
    Question question = Question.finder.all().stream().filter(q -> q.getTitle().equals(title)).findAny().orElse(null);
    if(question == null)
      question = new Question(id, title, author, text, maxPoints, exerciseType, answers);
    
    question.saveInDb();
    
    return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
  }
  
  public Result newQuestionForm(boolean isFreetext) {
    if(isFreetext)
      return ok(views.html.question.newFreetextQuestionForm.render(getUser()));
    
    // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
    boolean isChoice = true;
    return ok(views.html.question.newQuestionForm.render(getUser(), isChoice));
  }
  
  public Result question(int id) {
    User user = getUser();
    Question question = Question.finder.byId(id);
    
    if(question == null)
      return redirect(controllers.questions.routes.QuestionController.index(0));
    
    UserAnswer oldAnswer = UserAnswer.finder.byId(new UserAnswerKey(user.name, id));
    
    return ok(views.html.question.question.render(user, question, oldAnswer));
  }
  
  public Result questionResult(int id) {
    User user = getUser();
    Question question = Question.finder.byId(id);
    
    if(question.questionType == Question.QType.FREETEXT) {
      UserAnswerKey key = new UserAnswerKey(user.name, id);
      UserAnswer answer = UserAnswer.finder.byId(key);
      
      if(answer == null)
        answer = new UserAnswer(key);
      
      answer.question = Question.finder.byId(id);
      answer.text = factory.form().bindFromRequest().get("answer");
      answer.save();
      
      return ok(views.html.freetextQuestionResult.render(getUser(), question, answer));
    } else {
      DynamicForm form = factory.form().bindFromRequest();
      
      QuestionResult result = new QuestionResult(readSelAnswers(question, form), question);
      return ok(views.html.givenanswerQuestionResult.render(user, result));
    }
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
