package controllers.js;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.JsExercise;
import model.JsExerciseReader;
import model.JsTestData;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.preview;
import views.html.jsupload;

public class JsAdmin extends AdminController<JsExercise, JsExerciseReader> {
  
  @Inject
  public JsAdmin(Util theUtil) {
    super(theUtil, "js", new JsExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<JsExercise> exercises = exerciseReader.readStandardExercises();
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
    
    List<JsExercise> exercises = exerciseReader.readExercises(jsonFile);
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }
  
  @Override
  public Result uploadForm() {
    return ok(jsupload.render(UserManagement.getCurrentUser()));
  }
  
  @Override
  protected void saveExercises(List<JsExercise> exercises) {
    for(JsExercise ex: exercises) {
      ex.save();
      for(JsTestData test: ex.functionTests)
        test.save();
    }
  }
  
}
