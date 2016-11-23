package controllers.web;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.html.WebExercise;
import model.html.WebExerciseReader;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.webpreview;
import views.html.webupload;

public class WebAdmin extends AdminController {
  
  @Inject
  public WebAdmin(Util theUtil) {
    super(theUtil, "web");
  }

  @Override
  public Result create() {
    List<WebExercise> exercises = (new WebExerciseReader()).readExercises(jsonFile, jsonSchemaFile);
    for(WebExercise ex: exercises)
      ex.save();

    return ok(webpreview.render(UserManagement.getCurrentUser(), exercises));
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

    List<WebExercise> exercises = (new WebExerciseReader()).readExercises(jsonFile, jsonSchemaFile);
    for(WebExercise ex: exercises)
      ex.save();

    return ok(webpreview.render(UserManagement.getCurrentUser(), exercises));
  }

  @Override
  public Result uploadForm() {
    return ok(webupload.render(UserManagement.getCurrentUser()));
  }

}
