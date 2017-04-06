package controllers.choice;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import com.google.common.io.Files;

import controllers.core.AbstractAdminController;
import model.Answer;
import model.AnswerKey;
import model.Correctness;
import model.Question;
import model.QuestionReader;
import model.QuestionType;
import model.Quiz;
import model.Util;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class QuestionAdmin extends AbstractAdminController<Question, QuestionReader> {
  
  @Inject
  public QuestionAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "choice", new QuestionReader());
  }
  
  private static void assignQuestion(String keyAndValue, boolean addOrRemove) {
    String[] quizAndQuestion = keyAndValue.split("_");
    
    Quiz quiz = Quiz.finder.byId(Integer.parseInt(quizAndQuestion[0]));
    Question question = Question.finder.byId(Integer.parseInt(quizAndQuestion[1]));
    
    if(addOrRemove)
      quiz.questions.add(question);
    else
      quiz.questions.remove(question);
    
    quiz.save();
  }
  
  public Result assignQuestions() {
    DynamicForm form = factory.form().bindFromRequest();
    
    // Read it...
    Map<String, String> assignments = form.data();
    for(Map.Entry<String, String> entry: assignments.entrySet())
      assignQuestion(entry.getKey(), "on".equals(entry.getValue()));
    
    return ok(views.html.questionadmin.questionsAssigned.render(getUser(), assignments.toString()));
  }
  
  public Result assignQuestionsForm() {
    return ok(views.html.questionadmin.assignQuestionsForm.render(getUser(), Question.finder.all(), Quiz.finder.all()));
  }
  
  public Result editQuestion(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    
    Logger.debug("DATA:\n" + form.data());
    
    Question question = Question.finder.byId(id);
    
    question.title = form.get("title");
    question.text = form.get("text");
    question.author = getUser().name;
    question.questionType = QuestionType.valueOf(form.get("type"));
    
    int numOfAnswers = Integer.parseInt(form.get("numOfAnswers"));
    question.answers = new ArrayList<>(numOfAnswers);
    
    for(int count = 1; count <= numOfAnswers; count++) {
      AnswerKey key = new AnswerKey(id, count);
      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);
      answer.correctness = Correctness.valueOf(form.get("correctness_" + count));
      answer.text = form.get(Integer.toString(count));
      
      question.answers.add(answer);
    }
    
    question.save();
    for(Answer answer: question.answers)
      answer.save();
    
    return ok(views.html.questionadmin.choiceCreation.render(getUser(), Arrays.asList(question)));
  }
  
  public Result editQuestionForm(int id) {
    return ok(views.html.questionadmin.editQuestionForm.render(getUser(), Question.finder.byId(id)));
  }
  
  public Result exportQuestions() {
    String json = Json.prettyPrint(Json.toJson(Question.finder.all()));
    
    try {
      File tempFile = new File("questions_export_" + LocalDateTime.now() + ".json");
      Files.write(json, tempFile, Charset.defaultCharset());
      // false == download file!
      return ok(tempFile, false);
    } catch (IOException e) {
      return ok(json);
    }
  }
  
  public Result getJSONSchemaFile() {
    return ok(new File("conf/resources/choice/exerciseSchema.json"));
  }
  
  public Result index() {
    return ok(views.html.questionadmin.questionAdmin.render(getUser()));
  }
  
  public Result newQuiz() {
    DynamicForm form = factory.form().bindFromRequest();
    
    int id = findMinimalNotUsedId(Quiz.finder);
    String title = form.get("title");
    
    Quiz quiz;
    
    // Is there another quiz with the same title?
    List<Quiz> other = Quiz.finder.where().eq("title", title).findList();
    if(!other.isEmpty())
      quiz = other.get(0);
    else
      quiz = new Quiz(id);
    
    quiz.title = title;
    quiz.text = form.get("text");
    quiz.save();
    
    return ok(views.html.questionadmin.quizCreated.render(getUser(), quiz));
  }
  
  public Result newQuizForm() {
    return ok(views.html.questionadmin.newQuizForm.render(getUser()));
  }
  
  public Result notAssignedQuestions() {
    List<Question> notAssignedQuestions = Question.finder.all().stream().filter(q -> q.quizzes.isEmpty())
        .collect(Collectors.toList());
    
    return ok(views.html.questionadmin.notassignedquestions.render(getUser(), notAssignedQuestions));
  }
  
  public Result questions() {
    return ok(views.html.questionadmin.questions.render(getUser(), Question.finder.all()));
  }
  
  public Result quizzes() {
    return ok(views.html.questionadmin.quizzes.render(getUser(), Quiz.finder.all()));
  }
  
  @Override
  public Result readStandardExercises() {
    List<Question> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(views.html.questionadmin.choiceCreation.render(getUser(), exercises));
  }
  
  @Override
  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);
    if(uploadedFile == null)
      return badRequest("Fehler!");
    
    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), ADMIN_FOLDER, exerciseType);
    
    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);
    
    List<Question> exercises = exerciseReader.readExercises(jsonFile);
    saveExercises(exercises);
    // return ok(views.html.preview.render(getUser(),
    // views.html.jscreation.render(exercises)));
    
    return ok("TODO!");
  }
  
  @Override
  public Result uploadForm() {
    return ok("TODO!");
    // return ok(views.html.jsupload.render(getUser()));
  }
  
  @Override
  protected void saveExercises(List<Question> questions) {
    try {
      for(Question question: questions) {
        question.save();
        for(Answer answer: question.answers)
          answer.save();
      }
    } catch (RollbackException e) {
      Logger.error("FEHLER:", e);
    }
  }
  
}
