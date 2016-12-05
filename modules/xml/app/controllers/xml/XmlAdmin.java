package controllers.xml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.xmlpreview;
import views.html.xmlupload;

public class XmlAdmin extends AdminController<XmlExercise, XmlExerciseReader> {
  
  @Inject
  public XmlAdmin(Util theUtil) {
    super(theUtil, "xml", new XmlExerciseReader());
  }
  
  @Override
  public Result readStandardExercises() {
    List<XmlExercise> exercises = exerciseReader.readStandardExercises();
    saveExercises(exercises);
    return ok(xmlpreview.render(UserManagement.getCurrentUser(), exercises));
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
    
    return ok(xmlpreview.render(UserManagement.getCurrentUser(), exercises));
  }
  
  @Override
  public Result uploadForm() {
    return ok(xmlupload.render(UserManagement.getCurrentUser()));
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
