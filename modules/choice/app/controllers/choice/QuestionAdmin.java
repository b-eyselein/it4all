package controllers.choice;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import controllers.core.AdminController;
import model.Answer;
import model.Question;
import model.QuestionReader;
import model.Quiz;
import model.Util;
import play.Logger;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class QuestionAdmin extends AdminController<Question, QuestionReader> {

  @Inject
  public QuestionAdmin(Util theUtil) {
    super(theUtil, "choice", new QuestionReader());
  }

  public Result assignQuestionsForm() {
    List<Question> questions = Question.finder.all();
    List<Quiz> quizzes = Quiz.finder.all();
    return ok(views.html.assignQuestionsForm.render(getUser(), questions, quizzes));
  }

  @Override
  public Result readStandardExercises() {
    List<Question> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(views.html.choicecreation.render(getUser(), exercises));
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
