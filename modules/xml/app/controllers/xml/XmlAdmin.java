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
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class XmlAdmin extends AbstractAdminController<XmlExercise, XmlExerciseReader> {
  
  @Inject
  public XmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "xml", new XmlExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<XmlExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    
    return ok(views.html.preview.render(getUser(), views.html.xmlcreation.render(exercises)));
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
    
    List<XmlExercise> exercises = (new XmlExerciseReader()).readExercises(jsonFile);
    saveExercises(exercises);
    
    return ok(views.html.preview.render(getUser(), views.html.xmlcreation.render(exercises)));
  }
  
  @Override
  public Result uploadForm() {
    return ok(views.html.xmlupload.render(getUser()));
  }
  
  @Override
  protected void saveExercises(List<XmlExercise> exercises) {
    for(XmlExercise ex: exercises) {
      // FIXME: Aufgabendateien!
      ex.save();
      exerciseReader.checkOrCreateSampleFile(util, ex);
    }
  }
  
}
