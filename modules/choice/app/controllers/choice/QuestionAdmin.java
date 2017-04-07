package controllers.choice;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import com.google.common.io.Files;

import controllers.core.AbstractAdminController;
import model.Answer;
import model.Question;
import model.QuestionReader;
import model.Quiz;
import model.Util;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
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
  
  public Result exportQuizzes() {
    String json = Json.prettyPrint(Json.toJson(Quiz.finder.all()));
    
    try {
      File tempFile = new File("quizzes_export_" + LocalDateTime.now() + ".json");
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
    quiz.theme = form.get("theme");
    quiz.text = form.get("text");
    quiz.save();
    
    return ok(views.html.questionadmin.quizCreated.render(getUser(), quiz));
  }
  
  public Result newQuizForm() {
    return ok(views.html.questionadmin.newQuizForm.render(getUser()));
  }
  
  public Result notAssignedQuestions() {
    return ok(views.html.questionadmin.notassignedquestions.render(getUser(), Question.notAssignedQuestions()));
  }

  @Override
  public Result readStandardExercises() {
    AbstractReadingResult abstractResult = exerciseReader.readStandardExercises();
    
    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));
    
    @SuppressWarnings("unchecked")
    ReadingResult<Question> result = (ReadingResult<Question>) abstractResult;
    
    saveExercises(result.getRead());
    return ok(views.html.questionadmin.choiceCreation.render(getUser(), result.getRead()));
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
    
    AbstractReadingResult abstractResult = exerciseReader.readStandardExercises();
    
    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));
    
    @SuppressWarnings("unchecked")
    ReadingResult<Question> result = (ReadingResult<Question>) abstractResult;
    
    saveExercises(result.getRead());
    return ok(views.html.questionadmin.choiceCreation.render(getUser(), result.getRead()));
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
