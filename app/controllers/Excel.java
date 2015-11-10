package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import model.ExcelExercise;
import model.Student;
import model.spreadsheet.SpreadSheetCorrector;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.excel.excel;
import views.html.excel.excelcorrect;
import views.html.excel.exceloverview;

public class Excel extends Controller {
  
  public Result index() {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    List<ExcelExercise> exercises = ExcelExercise.finder.all();
    return ok(exceloverview.render(user, exercises));
  }
  
  public Result exercise(int exerciseId) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    if(exerciseId == -1 || ExcelExercise.finder.byId(exerciseId) == null)
      return redirect("/index");
    return ok(excel.render(user, ExcelExercise.finder.byId(exerciseId)));
  }
  
  public Result upload(int exerciseId) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    
    ExcelExercise exercise = ExcelExercise.finder.byId(exerciseId);
    
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart solutionFile = body.getFile("solFile");
    if(solutionFile != null) {
      
      Path path = solutionFile.getFile().toPath();
      String fileName = exercise.fileName;
      saveSolutionForUser(user.name, path, solutionFile.getFilename(), exerciseId);
      
      // FIXME: get Paths!
      String testPath = Util.getExcelSolFileForExercise(user.name, solutionFile.getFilename()).toString();
      String musterPath = Util.getExcelSampleDirectoryForExercise(exerciseId).toString();
      musterPath += "/" + fileName + "_Muster." + SpreadSheetCorrector.getExtension(testPath);
      String notice = SpreadSheetCorrector.startComparison(musterPath, testPath, fileName, false, false);
      
      return ok(excelcorrect.render(user, notice));
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
