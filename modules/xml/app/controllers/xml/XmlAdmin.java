package controllers.xml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.Util;
import model.XmlExercise;
import model.XmlExerciseReader;
import model.exercise.Exercise;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlAdmin extends AbstractAdminController<XmlExercise, XmlExerciseReader> {

  @Inject
  public XmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, XmlExercise.finder, "xml", new XmlExerciseReader());
  }

  public Result index() {
    return ok(views.html.xmlAdmin.index.render(getUser()));
  }

  public Result newExercise() {
    return ok("TODO!");
  }

  public Result newExerciseForm() {
    return ok(views.html.xmlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Html renderCreated(List<XmlExercise> exercises) {
    return views.html.xmlAdmin.xmlCreation.render(exercises);
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

    AbstractReadingResult<XmlExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<XmlExercise>) abstractResult));

    ReadingResult<XmlExercise> result = (ReadingResult<XmlExercise>) abstractResult;

    result.getRead().forEach(Exercise::saveInDB);
    return ok(views.html.preview.render(getUser(), views.html.xmlAdmin.xmlCreation.render(result.getRead())));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.xmlupload.render(getUser()));
  }

}
