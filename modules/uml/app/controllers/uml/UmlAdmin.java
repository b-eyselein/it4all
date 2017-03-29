package controllers.uml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.UmlExercise;
import model.UmlExerciseReader;
import model.Util;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UmlAdmin extends AdminController<UmlExercise, UmlExerciseReader> {
  
  @Inject
  public UmlAdmin(Util theUtil) {
    super(theUtil, "uml", new UmlExerciseReader());
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
