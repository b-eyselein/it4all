package controllers.questions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.common.io.Files;

import controllers.core.AbstractAdminController;
import model.FreetextAnswer;
import model.FreetextAnswerKey;
import model.QuestionReader;
import model.Quiz;
import model.question.Question;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class QuestionAdmin extends AbstractAdminController<Question, QuestionReader> {

  @Inject
  public QuestionAdmin(FormFactory theFactory) {
    super(theFactory, null, "question", new QuestionReader());
  }

  private static void assignQuestion(String keyAndValue, boolean addOrRemove) {
    // String[] quizAndQuestion = keyAndValue.split("_");
    //
    // Quiz quiz = Quiz.finder.byId(Integer.parseInt(quizAndQuestion[0]));
    // Question question =
    // Question.finder.byId(Integer.parseInt(quizAndQuestion[1]));
    //
    // if(addOrRemove)
    // quiz.questions.add(question);
    // else
    // quiz.questions.remove(question);
    //
    // quiz.save();
  }

  public Result assignQuestions() {
    DynamicForm form = factory.form().bindFromRequest();

    // Read it...
    Map<String, String> assignments = form.data();
    for(Map.Entry<String, String> entry: assignments.entrySet())
      assignQuestion(entry.getKey(), "on".equals(entry.getValue()));

    return ok(views.html.questionAdmin.questionsAssigned.render(getUser(), assignments.toString()));
  }

  public Result assignQuestionsForm() {
    return ok(views.html.questionAdmin.assignQuestionsForm.render(getUser(),
        /* Question.finder.all() */ Collections.emptyList(), Quiz.finder.all()));
  }

  public Result assignQuestionsSingleForm(int id) {
    return ok(views.html.questionAdmin.assignQuestionsForm.render(getUser(),
        /* Question.finder.all() */ Collections.emptyList(), Arrays.asList(Quiz.finder.byId(id))));
  }

  public Result exportQuizzes() {
    String json = Json.prettyPrint(Json.toJson(Quiz.finder.all()));

    try {
      File tempFile = new File("quizzes_export_" + LocalDateTime.now() + ".json");
      Files.asCharSink(tempFile, Charset.defaultCharset()).write(json);
      // false == download file!
      return ok(tempFile, false);
    } catch (IOException e) {
      return ok(json);
    }
  }

  @Override
  public Question getNew(int id) {
    return null; // new Question(id);
  }

  public Result gradeFreetextAnswer(int id, String user) {
    FreetextAnswer answer = FreetextAnswer.finder.byId(new FreetextAnswerKey(user, id));
    return ok(views.html.questionAdmin.ftaGradeForm.render(getUser(), answer));
  }

  public Result gradeFreetextAnswers() {
    return ok(views.html.questionAdmin.ftasToGrade.render(getUser(), FreetextAnswer.finder.all()));
  }

  public Result importQuizzes() {
    return ok("TODO!");
  }

  @Override
  public Result index() {
    return ok(views.html.questionAdmin.index.render(getUser()));
  }

  @Override
  public Result newExercise() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result newExerciseForm() {
    // TODO Auto-generated method stub
    return null;
  }

  public Result notAssignedQuestions() {
    return ok(views.html.questionList.render(getUser(),
        /* Question.notAssignedQuestions() */ Question.all()));
  }

  @Override
  public Html renderCreated(List<Question> created) {
    return views.html.questionAdmin.questionCreated.render(getUser(), created);
  }

  @Override
  protected void initRemainingExFromForm(DynamicForm form, Question exercise) {
    // TODO Auto-generated method stub

  }

}
