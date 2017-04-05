package controllers.js;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.JsExercise;
import model.JsExerciseReader;
import model.JsTestData;
import model.Util;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class JsAdmin extends AbstractAdminController<JsExercise, JsExerciseReader> {
  
  @Inject
  public JsAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "js", new JsExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<JsExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(views.html.preview.render(getUser(), views.html.jscreation.render(exercises)));
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
    return ok(views.html.preview.render(getUser(), views.html.jscreation.render(exercises)));
  }
  
  @Override
  public Result uploadForm() {
    return ok(views.html.jsupload.render(getUser()));
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
