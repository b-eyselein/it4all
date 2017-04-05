package controllers.python;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.PythonExercise;
import model.PythonExerciseReader;
import model.PythonTestData;
import model.Util;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class PythonAdmin extends AbstractAdminController<PythonExercise, PythonExerciseReader> {

  @Inject
  public PythonAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "Python", new PythonExerciseReader());
  }

  @Override
  public Result readStandardExercises() {
    List<PythonExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(views.html.preview.render(getUser(), views.html.pythoncreation.render(exercises)));
  }

  @Override
  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);
    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), ADMIN_FOLDER, exerciseType);

    Path pythononFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, pythononFile);

    List<PythonExercise> exercises = exerciseReader.readExercises(pythononFile);
    saveExercises(exercises);
    return ok(views.html.preview.render(getUser(), views.html.pythoncreation.render(exercises)));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.pythonupload.render(getUser()));
  }

  @Override
  protected void saveExercises(List<PythonExercise> exercises) {
    for(PythonExercise ex: exercises) {
      ex.save();
      for(PythonTestData test: ex.functionTests)
        test.save();
    }
  }

}
