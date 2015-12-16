package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.html.ElementResult;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.user.Student;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.empty;
import views.html.html.htmlcorrect;
import views.html.html.html;
import views.html.html.htmloverview;

public class HTML extends Controller {
  
  @Security.Authenticated(Secured.class)
  public Result exericse(int exercise) {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(student == null) {
      session().clear();
      return redirect("/login");
    }
    HtmlExercise exer = HtmlExercise.finder.byId(exercise);
    if(exer == null)
      return redirect("/html/");
    return ok(html.render(student, exer));
  }
  
  @Security.Authenticated(Secured.class)
  public Result index() {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    List<HtmlExercise> exercises = HtmlExercise.finder.all();
    return ok(htmloverview.render(exercises, student));
  }
  
  @Security.Authenticated(Secured.class)
  private void saveSolutionForUser(String user, String solution, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(user)))
        Files.createDirectory(Util.getSolDirForUser(user));
      
      Path solDir = Util.getSolDirForUserAndType("html", user);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);
      
      Path saveTo = Util.getHtmlSolFileForExercise(user, "html", exercise);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
  
  public Result site(String userName, int exercise) {
    List<String> strings = Arrays.asList("Es gab einen Fehler...");
    Path path = Util.getHtmlSolFileForExercise(userName, "html", exercise);
    if(Files.exists(path)) {
      try {
        strings = Files.readAllLines(path);
        for(String s: strings)
          s.trim();
      } catch (IOException e) {
      }
    }
    return ok(empty.render(new Html(String.join("\n", strings))));
  }
  
  @Security.Authenticated(Secured.class)
  public Result upload(int exercise) {
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    HtmlExercise ex = HtmlExercise.finder.byId(exercise);
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart htmlFile = body.getFile("solFile");
    if(htmlFile != null) {
      try {
        Path file = htmlFile.getFile().toPath();
        List<String> fileContent = Files.readAllLines(file);
        saveSolutionForUser(user.name, String.join("\n", fileContent), exercise);
        
        String url = "/solutions/" + user.name + "/html/" + exercise;
        List<ElementResult> result = HtmlCorrector.correct(url, ex, user);
        
        List<String> solution = Files.readAllLines(Util.getHtmlSolFileForExercise(user.name, "html", exercise));
        
        return ok(htmlcorrect.render(user, ex, result, solution));
      } catch (IOException e) {
        return badRequest("Datei konnte nicht hochgeladen werden!");
      }
    } else
      return badRequest("Datei konnte nicht hochgeladen werden!");
  }
}
