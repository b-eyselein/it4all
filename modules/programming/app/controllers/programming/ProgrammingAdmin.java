package controllers.programming;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.ProgrammingExercise;
import model.Util;
import model.ProgrammingExerciseReader;
import model.TestData;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.preview;

public class ProgrammingAdmin extends AdminController<ProgrammingExercise, ProgrammingExerciseReader> {

  @Inject
  public ProgrammingAdmin(Util theUtil) {
    super(theUtil, "prog", new ProgrammingExerciseReader());
  }

  @Override
  public Result readStandardExercises() {
    List<ProgrammingExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }

  @Override
  protected void saveExercises(List<ProgrammingExercise> exercises) {
    for(ProgrammingExercise ex: exercises) {
      ex.save();
      for(TestData test: ex.functiontests)
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
    
    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    List<ProgrammingExercise> exercises = exerciseReader.readExercises(jsonFile);
    saveExercises(exercises);
    return ok(preview.render(UserManagement.getCurrentUser(), new LinkedList<>(exercises)));
  }

  @Override
  public Result uploadForm() {
    return ok("TODO!");
    // return ok(Programmingupload.render(UserManagement.getCurrentUser()));
  }

}
