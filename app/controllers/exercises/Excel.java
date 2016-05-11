package controllers.exercises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import model.spreadsheet.ExcelExercise;
import model.spreadsheet.SpreadSheetCorrectionResult;
import model.spreadsheet.SpreadSheetCorrector;
import model.user.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.excel.excel;
import views.html.excel.excelcorrect;
import views.html.excel.exceloverview;
import controllers.core.UserControl;
import controllers.core.Util;

@Security.Authenticated(Secured.class)
public class Excel extends Controller {
  
  public Result download(int exerciseId, String typ) {
    User user = UserControl.getCurrentUser();
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    Path fileToDownload = Paths.get("/var/lib/it4all/solutions", user.getName(), "excel",
        exercise.fileName + "_Korrektur." + typ);
    if(Files.exists(fileToDownload))
      return ok(fileToDownload.toFile());
    else
      return badRequest("Korrigierte Datei existiert nicht!");
  }
  
  public Result exercise(int exerciseId) {
    User user = UserControl.getCurrentUser();
    if(exerciseId == -1 || ExcelExercise.finder.byId(exerciseId) == null)
      return redirect("/index");
    return ok(excel.render(user, ExcelExercise.finder.byId(exerciseId)));
  }
  
  public Result index() {
    User user = UserControl.getCurrentUser();
    List<ExcelExercise> exercises = ExcelExercise.finder.all();
    return ok(exceloverview.render(user, exercises));
  }
  
  public Result upload(int exerciseId) {
    User user = UserControl.getCurrentUser();
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart solutionFile = body.getFile("solFile");
    if(solutionFile != null) {
      
      Path path = solutionFile.getFile().toPath();
      String fileName = exercise.fileName;
      saveSolutionForUser(user.getName(), path, solutionFile.getFilename(), exerciseId);
      
      // FIXME: get Paths!
      Path testPath = Util.getExcelSolFileForExercise(user.getName(), solutionFile.getFilename());
      Path musterPath = Util.getExcelSampleDirectoryForExercise(exerciseId);
      musterPath = Paths.get(musterPath.toString(),
          fileName + "_Muster." + SpreadSheetCorrector.getExtension(testPath));
      SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(musterPath, testPath, false, false);
      
      return ok(excelcorrect.render(user, result, exerciseId, SpreadSheetCorrector.getExtension(testPath)));
    } else {
      return badRequest("Datei konnte nicht hochgeladen werden!");
    }
  }
  
  private void saveSolutionForUser(String user, Path uploadedSolution, String fileName, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(user)))
        Files.createDirectory(Util.getSolDirForUser(user));
      Path solDir = Util.getSolDirForUserAndType("excel", user);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);
      
      Path targetFile = Util.getExcelSolFileForExercise(user, fileName);
      Files.move(uploadedSolution, targetFile, StandardCopyOption.REPLACE_EXISTING);
      
    } catch (IOException e) {
      System.out.println(e);
    }
  }
  
}
