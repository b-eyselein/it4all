package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import controllers.core.UserControl;
import controllers.core.Util;
import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.html.html;
import views.html.html.htmloverview;

public class HTML extends Controller {
  
  private static String serverUrl = Util.getServerUrl();
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  
  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserControl.getUser();
    
    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user.getName(), learnerSolution, exerciseId);
    
    String json = correctExercise(user, HtmlExercise.finder.byId(exerciseId));
    
    if(request().accepts("application/json"))
      return ok(json).as("application/json");
    else
      return ok("TODO!");
  }
  
  @Security.Authenticated(Secured.class)
  public Result exercise(int exerciseId) {
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);
    if(exercise == null)
      return redirect(controllers.web.routes.HTML.index());
    else
      return ok(html.render(UserControl.getUser(), exercise, serverUrl));
  }
  
  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(htmloverview.render(HtmlExercise.finder.all(), UserControl.getUser()));
  }
  
  public Result site(String userName, int exercise) {
    Path file = Util.getHtmlSolFileForExercise(userName, "html", exercise);
    if(!Files.exists(file))
      return badRequest("Fehler: Datei nicht vorhanden!");
    
    try {
      // IMPORTANT: return HTML!
      return ok(new Html(String.join("\n", Files.readAllLines(file))));
    } catch (IOException e) {
      // TODO: Log Error!?!
      // --> Evtl. entsprechend Fehler werfen bzw. in Korrektor auffangen!
      return badRequest("Fehler beim Lesen der Datei!");
    }
    
  }
  
  private String correctExercise(User user, HtmlExercise exercise) {
    String solutionUrl = routes.HTML.site(user.getName(), exercise.id).absoluteURL(request());
    
    List<ElementResult<? extends Task>> result = HtmlCorrector.correct(solutionUrl, exercise, user);
    
    List<String> results = result.stream().map(res -> res.toJSON()).collect(Collectors.toList());
    
    String ret = "{\"results\": [\n" + String.join(",\n", results) + "\n]}";
    
    return ret;
  }
  
  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }
  
  private void saveSolutionForUser(String userName, String solution, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(userName)))
        Files.createDirectory(Util.getSolDirForUser(userName));
      
      Path solDir = Util.getSolDirForUserAndType("html", userName);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);
      
      Path saveTo = Util.getHtmlSolFileForExercise(userName, "html", exercise);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      // TODO: Log exception!
      System.out.println(e);
    }
  }
}
