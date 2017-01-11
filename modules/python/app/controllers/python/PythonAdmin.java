package controllers.python;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.PythonExercise;
import model.PythonExerciseReader;
import model.PythonTestData;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.preview;
import views.html.pythonupload;

public class PythonAdmin extends AdminController<PythonExercise, PythonExerciseReader> {
  
  @Inject
  public PythonAdmin(Util theUtil) {
    super(theUtil, "Python", new PythonExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<PythonExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }
  
  @Override
  protected void saveExercises(List<PythonExercise> exercises) {
    for(PythonExercise ex: exercises) {
      ex.save();
      for(PythonTestData test: ex.functionTests)
        test.save();
    }
  }
  
  @Override
  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);
    if(uploadedFile == null)
      return badRequest("Fehler!");
    
    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), ADMIN_FOLDER, exerciseType);
    
    Path PythononFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, PythononFile);
    
    List<PythonExercise> exercises = exerciseReader.readExercises(PythononFile);
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }
  
  @Override
  public Result uploadForm() {
    return ok(pythonupload.render(UserManagement.getCurrentUser()));
  }
  
}
