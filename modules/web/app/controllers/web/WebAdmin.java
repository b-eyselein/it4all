package controllers.web;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.WebExercise;
import model.WebExerciseReader;
import model.task.JsWebTask;
import model.task.Condition;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.preview;
import views.html.webupload;

public class WebAdmin extends AdminController<WebExercise, WebExerciseReader> {
  
  @Inject
  public WebAdmin(Util theUtil) {
    super(theUtil, "web", new WebExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<WebExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
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
    
    List<WebExercise> exercises = exerciseReader.readExercises(jsonFile);
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }
  
  @Override
  public Result uploadForm() {
    return ok(webupload.render(UserManagement.getCurrentUser()));
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
