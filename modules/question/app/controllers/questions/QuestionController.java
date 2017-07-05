package controllers.questions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.BaseExerciseController;
import controllers.core.ExerciseController;
import model.FreetextAnswer;
import model.FreetextAnswerKey;
import model.QuestionResult;
import model.QuestionUser;
import model.Quiz;
import model.StringConsts;
import model.question.Answer;
import model.question.AnswerKey;
import model.question.Correctness;
import model.question.FreetextQuestion;
import model.question.GivenAnswerQuestion;
import model.question.Question;
import model.user.Role;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class QuestionController extends BaseExerciseController {
  
  private static final String ATTEMPT_FIELD = "attempt";
  private static final String QUIZ_ID_FIELD = "quizId";
  
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
  
  private static List<Answer> readAnswersFromForm(DynamicForm form, Question question, boolean isChoice) {
    return IntStream.range(0, GivenAnswerQuestion.MAX_ANSWERS).mapToObj(id -> {
      AnswerKey key = new AnswerKey(question.id, id);
      
      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);
      
      answer.text = form.get(String.valueOf(id));
      answer.correctness = isChoice ? Correctness.valueOf(form.get("correctness_" + id)) : Correctness.CORRECT;
      
      return answer;
    }).filter(ans -> !ans.text.isEmpty()).collect(Collectors.toList());
  }
  
  private static List<Answer> readSelAnswers(GivenAnswerQuestion question, DynamicForm form) {
    return question.answers.stream().filter(ans -> form.get(Integer.toString(ans.key.id)) != null)
        .collect(Collectors.toList());
  }
  
  public Result editQuestion(int id, boolean isFreetext) {
    // DynamicForm form = factory.form().bindFromRequest();
    
    Question question = null; // readQuestionFromForm(form,
                              // Question.finder.byId(id));
    // question.save();
    // for(Answer answer: question.answers)
    // answer.save();
    
    return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
  }
  
  public Result editQuestionForm(int id, boolean isFreetext) {
    User user = getUser();
    Question question;
    if(isFreetext)
      question = FreetextQuestion.finder.byId(id);
    else
      question = GivenAnswerQuestion.finder.byId(id);
    
    if(question.author.equals(user.name) || user.stdRole == Role.ADMIN)
      return ok(views.html.editQuestionForm.render(user, question, true));
    
    return redirect(routes.QuestionController.index());
  }
  
  public Result freetextQuestion(int id) {
    User user = getUser();
    FreetextQuestion question = FreetextQuestion.finder.byId(id);
    FreetextAnswer answer = FreetextAnswer.finder.byId(new FreetextAnswerKey(user.name, id));
    return ok(views.html.question.freetextQuestion.render(user, question, answer));
  }
  
  public Result freetextQuestionResult(int id) {
    User user = getUser();
    
    FreetextAnswerKey key = new FreetextAnswerKey(user.name, id);
    FreetextAnswer answer = FreetextAnswer.finder.byId(key);
    
    if(answer == null)
      answer = new FreetextAnswer(key);
    
    answer.question = FreetextQuestion.finder.byId(id);
    answer.answer = factory.form().bindFromRequest().get("answer");
    answer.save();
    
    return ok(views.html.freetextQuestionResult.render(getUser(), answer));
  }
  
  public Result index() {
    return ok(views.html.questionIndex.render(getUser(), Quiz.finder.all()));
  }
  
  public Result newQuestion(boolean isFreetext) {
    DynamicForm form = factory.form().bindFromRequest();
    
    String title = form.get(StringConsts.TITLE_NAME);
    
    Question question;
    
    if(isFreetext) {
      question = FreetextQuestion.finder.all().stream().filter(ftq -> ftq.title.equals(title)).findAny().orElse(null);
      if(question == null)
        question = new FreetextQuestion(findMinimalNotUsedId(GivenAnswerQuestion.finder));
    } else {
      question = GivenAnswerQuestion.finder.all().stream().filter(gaq -> gaq.title.equals(title)).findAny()
          .orElse(null);
      if(question == null)
        question = new GivenAnswerQuestion(findMinimalNotUsedId(GivenAnswerQuestion.finder));
      
      boolean isChoice = true; // TODO!
      ((GivenAnswerQuestion) question).answers = readAnswersFromForm(form, question, isChoice);
    }
    
    question.title = title;
    question.text = form.get(StringConsts.TEXT_NAME);
    question.author = form.get(StringConsts.AUTHOR_NAME);
    
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
    GivenAnswerQuestion question = GivenAnswerQuestion.finder.byId(id);
    // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
    boolean isChoice = true;
    
    if(question == null)
      return redirect(controllers.questions.routes.QuestionController.index());
    
    return ok(views.html.question.question.render(getUser(), question, isChoice));
  }
  
  public Result questionResult(int id) {
    User user = getUser();
    GivenAnswerQuestion question = GivenAnswerQuestion.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    
    QuestionResult result = new QuestionResult(readSelAnswers(question, form), question);
    return ok(views.html.givenanswerQuestionResult.render(user, result));
  }
  
  public Result questions() {
    return ok(views.html.questionList.render(getUser(), Question.all()));
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
