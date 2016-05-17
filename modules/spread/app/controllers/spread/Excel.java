package controllers.spread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import controllers.core.UserControl;
import controllers.core.Util;
import model.spread.ExcelExercise;
import model.spread.SpreadSheetCorrectionResult;
import model.spread.SpreadSheetCorrector;
import model.user.Secured;
import model.user.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.excel.excel;
import views.html.excel.excelcorrect;
import views.html.excel.exceloverview;
import views.html.excel.spreadcorrectionerror;

@Security.Authenticated(Secured.class)
public class Excel extends Controller {
  
  private static final String BODY_SOL_FILE_NAME = "solFile";
  
  public Result download(int exerciseId, String typ) {
    User user = UserControl.getCurrentUser();
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    Path dirForCorrectedFile = Util.getSolDirForUserAndType("excel", user.name);
    File fileToDownload = Paths.get(dirForCorrectedFile.toString(), exercise.fileName + "_Korrektur." + typ).toFile();
    
    if(!fileToDownload.exists())
      return badRequest(new Html("<p>Die Korrigierte Datei existiert nicht!</p><p>Zur&uuml;ck zur <a href=\""
          + routes.Excel.index() + "\">&Uuml;bersichtsseite</a></p>"));
    
    return ok(fileToDownload);
  }
  
  public Result exercise(int exerciseId) {
    User user = UserControl.getCurrentUser();
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.Excel.index() + "\">Startseite</a>.</p>"));
    
    return ok(excel.render(user, exercise));
  }
  
  public Result index() {
    return ok(exceloverview.render(ExcelExercise.finder.all(), UserControl.getCurrentUser()));
  }
  
  public Result upload(int exerciseId) {
    User user = UserControl.getCurrentUser();
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    // Extract solution from request
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart uploadedFile = body.getFile(BODY_SOL_FILE_NAME);
    if(uploadedFile == null)
      return internalServerError(spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"));
    
    // Save solution
    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path targetFilePath = Util.getExcelSolFileForExercise(user.name, uploadedFile.getFilename());
    boolean fileSuccessfullySaved = saveSolutionForUser(user.name, pathToUploadedFile, targetFilePath);
    if(!fileSuccessfullySaved)
      return internalServerError(spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"));
    
    // Get paths to sample document
    Path sampleDocumentDirectoryPath = Util.getExcelSampleDirectoryForExercise("excel", exercise.id);
    Path sampleDocumentPath = Paths.get(sampleDocumentDirectoryPath.toString(),
        exercise.fileName + "_Muster." + SpreadSheetCorrector.getExtension(targetFilePath));
    if(!Files.exists(sampleDocumentPath))
      return internalServerError(spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"));
    
    SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false, false);
    
    if(result.isSuccess())
      return ok(excelcorrect.render(user, result, exercise.id, SpreadSheetCorrector.getExtension(targetFilePath)));
    else
      return internalServerError(spreadcorrectionerror.render(user, result.getNotices().get(0)));
    
  }
  
  private boolean saveSolutionForUser(String user, Path uploadedSolution, Path targetFilePath) {
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
  
}
