package controllers.web;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.Util;
import model.WebExercise;
import model.WebExerciseReader;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import model.task.Condition;
import model.task.JsWebTask;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class WebAdmin extends AbstractAdminController<WebExercise, WebExerciseReader> {
  
  @Inject
  public WebAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "web", new WebExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    AbstractReadingResult abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    ReadingResult<WebExercise> result = (ReadingResult<WebExercise>) abstractResult;
    
    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.webcreation.render(result.getRead())));
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
    ReadingResult<WebExercise> result = (ReadingResult<WebExercise>) abstractResult;
    
    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.webcreation.render(result.getRead())));
  }
  
  @Override
  public Result uploadForm() {
    return ok(views.html.webupload.render(getUser()));
  }
  
  @Override
  protected void saveExercises(List<WebExercise> exercises) {
    
    for(WebExercise ex: exercises) {
      ex.save();
      for(JsWebTask task: ex.jsTasks)
        for(Condition cond: task.conditions)
          cond.save();
    }
  }
  
}
