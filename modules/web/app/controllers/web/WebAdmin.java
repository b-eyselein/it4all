package controllers.web;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.Util;
import model.WebExercise;
import model.WebExerciseReader;
import model.exercise.Exercise;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class WebAdmin extends AbstractAdminController<WebExercise, WebExerciseReader> {

  @Inject
  public WebAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, WebExercise.finder, "web", new WebExerciseReader());
  }

  public Result index() {
    return ok(views.html.webAdmin.index.render(getUser()));
  }

  @Override
  public Html renderCreated(List<WebExercise> created) {
    return views.html.webAdmin.webcreation.render(created);
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

    AbstractReadingResult<WebExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<WebExercise>) abstractResult));

    ReadingResult<WebExercise> result = (ReadingResult<WebExercise>) abstractResult;

    result.getRead().forEach(Exercise::saveInDB);
    return ok(views.html.preview.render(getUser(), views.html.webAdmin.webcreation.render(result.getRead())));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.webAdmin.webupload.render(getUser()));
  }

}
