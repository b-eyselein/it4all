package controllers.uml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.UmlExercise;
import model.UmlExerciseReader;
import model.Util;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UmlAdmin extends AbstractAdminController<UmlExercise, UmlExerciseReader> {

  @Inject
  public UmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "uml", new UmlExerciseReader());
  }

  @Override
  public Result readStandardExercises() {
    List<UmlExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);

    return ok(views.html.preview.render(getUser(), views.html.umlcreation.render(exercises)));
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

    List<UmlExercise> exercises = (new UmlExerciseReader()).readExercises(jsonFile);
    saveExercises(exercises);

    return ok(views.html.preview.render(getUser(), views.html.umlcreation.render(exercises)));
  }

  @Override
  public Result uploadForm() {
    return ok("TODO!");
    // return ok(views.html.umlupload.render(getUser()));
  }

  @Override
  protected void saveExercises(List<UmlExercise> exercises) {
    for(UmlExercise ex: exercises) {
      // FIXME: Aufgabendateien!
      ex.save();
      // exerciseReader.checkOrCreateSampleFile(util, ex);
    }
  }

}
