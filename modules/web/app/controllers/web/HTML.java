package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

//import controllers.Application;

import controllers.Util;
import model.html.ElementResult;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.user.Secured;
import model.user.Student;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.html.htmlcorrect;
import views.html.html.html;
import views.html.html.htmloverview;
import model.user.UserControl;

public class HTML extends Controller {
  
  @Security.Authenticated(Secured.class)
  public Result exercise(int exercise) {
    User student = UserControl.getUser();
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
    User student = UserControl.getUser();
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
    return ok(new Html(String.join("\n", strings)));
  }
  
  @Security.Authenticated(Secured.class)
  public Result upload(int exercise) {
    User user = UserControl.getUser();
    HtmlExercise ex = HtmlExercise.finder.byId(exercise);
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart htmlFile = body.getFile("solFile");
    if(htmlFile != null) {
      try {
        Path file = htmlFile.getFile().toPath();
        List<String> fileContent = Files.readAllLines(file);
        saveSolutionForUser(user.getName(), String.join("\n", fileContent), exercise);
        
        String url = "/web/solutions/" + user.getName() + "/html/" + exercise;
        List<ElementResult> result = HtmlCorrector.correct(url, ex, user);
        
        List<String> solution = Files.readAllLines(Util.getHtmlSolFileForExercise(user.getName(), "html", exercise));
        
        return ok(htmlcorrect.render(user, ex, result, solution));
      } catch (IOException e) {
        return badRequest("Datei konnte nicht hochgeladen werden!");
      }
    } else
      return badRequest("Datei konnte nicht hochgeladen werden!");
  }
}
