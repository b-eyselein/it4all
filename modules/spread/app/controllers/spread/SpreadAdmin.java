package controllers.spread;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.spreadpreview;
import views.html.spreadupload;

public class SpreadAdmin extends AdminController {

  @Inject
  public SpreadAdmin(Util theUtil) {
    super(theUtil, "spread");
  }
  
  @Override
  public Result create() {
    List<SpreadExercise> exercises = (new SpreadExerciseReader()).readExercises(jsonFile, jsonSchemaFile);
    for(SpreadExercise ex: exercises)
      ex.save();
    
    return ok(spreadpreview.render(UserManagement.getCurrentUser(), exercises));
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

    List<SpreadExercise> exercises = (new SpreadExerciseReader()).readExercises(jsonFile, jsonSchemaFile);
    for(SpreadExercise ex: exercises)
      ex.save();
    
    return ok(spreadpreview.render(UserManagement.getCurrentUser(), exercises));
  }
  
  @Override
  public Result uploadForm() {
    return ok(spreadupload.render(UserManagement.getCurrentUser()));
  }
  
}
