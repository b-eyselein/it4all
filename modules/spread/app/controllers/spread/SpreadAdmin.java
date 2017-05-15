package controllers.spread;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.Util;
import model.exercise.Exercise;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadAdmin extends AbstractAdminController<SpreadExercise, SpreadExerciseReader> {
  
  @Inject
  public SpreadAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, SpreadExercise.finder, "spread", new SpreadExerciseReader());
  }
  
  @Override
  public Html renderCreated(List<SpreadExercise> created) {
    return views.html.spreadcreation.render(created);
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
    
    AbstractReadingResult<SpreadExercise> abstractResult = exerciseReader.readStandardExercises();
    
    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<SpreadExercise>) abstractResult));
    
    ReadingResult<SpreadExercise> result = (ReadingResult<SpreadExercise>) abstractResult;
    
    result.getRead().forEach(Exercise::saveInDB);
    return ok(views.html.preview.render(getUser(), views.html.spreadcreation.render(result.getRead())));
  }
  
  @Override
  public Result uploadForm() {
    return ok(views.html.spreadupload.render(getUser()));
  }
  
}
