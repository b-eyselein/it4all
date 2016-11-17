package controllers.spread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.Secured;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.SpreadSheetCorrectionResult;
import model.SpreadSheetCorrector;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.error;
import views.html.excel.excelcorrect;
import views.html.excel.spreadcorrectionerror;
import views.html.excel.spreadoverview;

@Security.Authenticated(Secured.class)
public class Spread extends ExerciseController<IntExerciseIdentifier> {
  
  private static final String EXERCISE_TYPE = "spread";
  private static final String BODY_SOL_FILE_NAME = "solFile";

  private static final String EXERCISE_FOLDER = "conf/resources/spread";
  private static final Path JSON_FILE = Paths.get(EXERCISE_FOLDER, "exercises.json");
  private static final Path JSON_SCHEMA_FILE = Paths.get(EXERCISE_FOLDER, "exerciseSchema.json");

  @Inject
  public Spread(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
    
    List<SpreadExercise> exercises = (new SpreadExerciseReader()).readExercises(JSON_FILE, JSON_SCHEMA_FILE);
    for(SpreadExercise ex: exercises) {
      ex.save();
      SpreadExerciseReader.checkFiles(util, ex);
    }
  }
  
  public Result download(IntExerciseIdentifier identifier, String typ) {
    User user = UserManagement.getCurrentUser();
    SpreadExercise exercise = SpreadExercise.finder.byId(identifier.id);
    
    if(exercise == null)
      return badRequest("This exercise does not exist!");
    
    Path fileToDownload = Paths.get(util.getSolDirForUserAndType(user, EXERCISE_TYPE).toString(),
        exercise.templateFilename + "_Korrektur." + typ);
    
    if(!Files.exists(fileToDownload))
      return badRequest(
          error.render(user, new Html("<p>Die Korrigierte Datei existiert nicht!</p><p>Zur&uuml;ck zur <a href=\""
              + routes.Spread.index() + "\">&Uuml;bersichtsseite</a></p>")));
    
    return ok(fileToDownload.toFile());
  }

  public Result downloadTemplate(IntExerciseIdentifier identifier, String fileType) {
    SpreadExercise exercise = SpreadExercise.finder.byId(identifier.id);
    
    if(exercise == null)
      return badRequest("This exercise does not exist!");
    
    Path filePath = Paths.get(util.getSampleDirForExerciseType(EXERCISE_TYPE).toString(),
        exercise.templateFilename + "." + fileType);
    
    
    if(!Files.exists(filePath))
      return badRequest("This file does not exist!");
    
    return ok(filePath.toFile());
  }
  
  public Result index() {
    return ok(spreadoverview.render(UserManagement.getCurrentUser(), SpreadExercise.finder.all()));
  }
  
  public Result upload(IntExerciseIdentifier identifier) {
    User user = UserManagement.getCurrentUser();
    SpreadExercise exercise = SpreadExercise.finder.byId(identifier.id);
    
    // Extract solution from request
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_SOL_FILE_NAME);
    if(uploadedFile == null)
      return internalServerError(spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"));
    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    
    String fileExtension = SpreadSheetCorrector.getExtension(uploadedFile.getFilename());
    
    // Save solution
    Path targetFilePath = util.getSolFileForExercise(user, EXERCISE_TYPE,
        exercise.templateFilename + "." + fileExtension);
    boolean fileSuccessfullySaved = saveSolutionForUser(pathToUploadedFile, targetFilePath);
    if(!fileSuccessfullySaved)
      return internalServerError(spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"));
    
    // Get paths to sample document
    Path sampleDocumentPath = Paths.get(util.getSampleDirForExerciseType(EXERCISE_TYPE).toString(),
        exercise.sampleFilename + "." + fileExtension);
    if(!Files.exists(sampleDocumentPath))
      return internalServerError(spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"));
    
    SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false, false);
    
    if(result.isSuccess())
      return ok(excelcorrect.render(user, result, exercise.id, fileExtension));
    else
      return internalServerError(spreadcorrectionerror.render(user, result.getNotices().get(0)));
    
  }
  
  private boolean saveSolutionForUser(Path uploadedSolution, Path targetFilePath) {
    try {
      Path solDirForExercise = targetFilePath.getParent();
      if(!Files.exists(solDirForExercise) && !Files.isDirectory(solDirForExercise))
        Files.createDirectories(solDirForExercise);
      
      Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (Exception e) {
      Logger.error("Fehler beim Speichern der Lösung!", e);
      return false;
    }
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier exercise) {
    // FIXME: implement!
    return null;
  }
  
}
