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
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class XmlAdmin extends AbstractAdminController<XmlExercise, XmlExerciseReader> {

  @Inject
  public XmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "xml", new XmlExerciseReader());
  }

  public Result index() {
    return ok(views.html.xmlAdmin.xmlAdmin.render(getUser()));
  }

  public Result newExercise() {
    return ok("TODO!");
  }

  public Result newExerciseForm() {
    return ok(views.html.xmlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Result readStandardExercises() {
    AbstractReadingResult<XmlExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<XmlExercise>) abstractResult));

    ReadingResult<XmlExercise> result = (ReadingResult<XmlExercise>) abstractResult;

    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.xmlcreation.render(result.getRead())));
  }

  @Override
  protected void saveExercises(List<XmlExercise> exercises) {
    for(XmlExercise ex: exercises) {
      // FIXME: Aufgabendateien!
      ex.save();
      exerciseReader.checkOrCreateSampleFile(util, ex);
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

    AbstractReadingResult<XmlExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<XmlExercise>) abstractResult));

    ReadingResult<XmlExercise> result = (ReadingResult<XmlExercise>) abstractResult;

    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.xmlcreation.render(result.getRead())));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.xmlupload.render(getUser()));
  }

}
