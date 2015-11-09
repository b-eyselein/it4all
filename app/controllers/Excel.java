package controllers;

import java.nio.file.Path;

import model.Student;
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
    return ok(exceloverview.render(user));
  }
  
  public Result exercise(int exercise) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    if(exercise == -1)
      return redirect("/index");
    return ok(excel.render(user, exercise));
  }
  
  public Result upload(int exercise) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(user == null) {
      session().clear();
      return redirect("/login");
    }
    
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart solutionFile = body.getFile("solFile");
    if(solutionFile != null) {
      Path path = solutionFile.getFile().toPath();
      System.out.println(path);
      // FIXME: Save file with correct name!
      
      // List<String> fileContent = Files.readAllLines(file);
      // saveSolutionForUser(user.name, String.join("\n", fileContent),
      // exercise);
      //
      // String url = "/solutions/" + user.name + "/html/" + exercise;
      // List<ElementResult> result = HtmlCorrector.correct(url, ex, user);
      //
      // List<String> solution =
      // Files.readAllLines(Util.getSolFileForExercise(user.name, exercise));
      
      return ok(excelcorrect.render(user));
    } else {
      return badRequest("Datei konnte nicht hochgeladen werden!");
    }
  }
  
}
