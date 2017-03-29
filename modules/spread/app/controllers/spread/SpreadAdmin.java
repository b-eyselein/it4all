package controllers.spread;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.Util;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class SpreadAdmin extends AdminController<SpreadExercise, SpreadExerciseReader> {

  @Inject
  public SpreadAdmin(Util theUtil) {
    super(theUtil, "spread", new SpreadExerciseReader());
  }

  @Override
  public Result readStandardExercises() {
    List<SpreadExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);

    return ok(views.html.preview.render(getUser(), views.html.spreadcreation.render(exercises)));
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

    List<SpreadExercise> exercises = exerciseReader.readExercises(jsonFile);
    saveExercises(exercises);
    return ok(views.html.preview.render(getUser(), views.html.spreadcreation.render(exercises)));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.spreadupload.render(getUser()));
  }

  @Override
  protected void saveExercises(List<SpreadExercise> exercises) {
    // FIXME: Dateien!
    for(SpreadExercise ex: exercises) {
      ex.save();
      exerciseReader.checkFiles(util, ex);
    }
  }

}
