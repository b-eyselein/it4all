package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.html.ElementResult;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.user.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.html.html;
import views.html.html.htmlcorrect;
import views.html.html.htmloverview;
import controllers.core.UserControl;
import controllers.core.Util;

public class HTML extends Controller {
  
  @Security.Authenticated(Secured.class)
  public Result exercise(int exercise) {
    HtmlExercise exer = HtmlExercise.finder.byId(exercise);
    if(exer == null)
      return redirect("/html/");
    return ok(html.render(UserControl.getUser(), exer));
  }
  
  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(htmloverview.render(HtmlExercise.finder.all(), UserControl.getUser()));
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
      // TODO: Log exception!
      System.out.println(e);
    }
  }
  
  public Result site(String userName, int exercise) {
    Path file = Util.getHtmlSolFileForExercise(userName, "html", exercise);
    if(Files.exists(file))
      return ok(file.toFile());
    else
      return badRequest("Fehler: Datei nicht vorhanden!");
  }
  
  @Security.Authenticated(Secured.class)
  public Result upload(int exerciseId) {
    User user = UserControl.getUser();
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);
    
    FilePart htmlFile = request().body().asMultipartFormData().getFile("solFile");
    if(htmlFile == null)
      return badRequest("Datei konnte nicht hochgeladen werden!");
    
    try {
      Path file = htmlFile.getFile().toPath();
      List<String> fileContent = Files.readAllLines(file);
      saveSolutionForUser(user.getName(), String.join("\n", fileContent), exerciseId);
      
      String url = "/web/solutions/" + user.getName() + "/html/" + exerciseId;
      List<ElementResult> result = HtmlCorrector.correct(url, exercise, user);
      
      List<String> solution = Files.readAllLines(Util.getHtmlSolFileForExercise(user.getName(), "html", exerciseId));
      
      return ok(htmlcorrect.render(user, exercise, result, solution));
    } catch (IOException e) {
      return badRequest("Datei konnte nicht hochgeladen werden!");
    }
  }
}
