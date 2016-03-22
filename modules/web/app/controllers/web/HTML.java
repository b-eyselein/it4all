package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.html.HtmlCorrector;
import model.html.HtmlExercise;
import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Secured;
import model.user.User;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Request;
import play.twirl.api.Html;
import views.html.html.html;
import views.html.html.htmloverview;
import controllers.core.UserControl;
import controllers.core.Util;

public class HTML extends Controller {
  
  private static String serverUrl = Util.getServerUrl();
  private static String learnerSolValue = "editorContent";
  
  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserControl.getUser();
    
    String learnerSolution = extractLearnerSolutionFromRequest(request(), learnerSolValue);
    saveSolutionForUser(user.getName(), learnerSolution, exerciseId);
    
    String json = correctExercise(user, HtmlExercise.finder.byId(exerciseId));
    return ok(json).as("application/json");
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
    if(Files.exists(file))
      try {
        // IMPORTANT: return HTML!
        return ok(new Html(String.join("\n", Files.readAllLines(file))));
      } catch (IOException e) {
        // TODO: Log Error!?!
        // --> Evtl. entsprechend Fehler werfen bzw. in Korrektor auffangen!
        return badRequest("Fehler beim Lesen der Datei!");
      }
    
    else
      return badRequest("Fehler: Datei nicht vorhanden!");
  }
  
  private String correctExercise(User user, HtmlExercise exercise) {
    // FIXME: implement!
    String url = "/web/solutions/" + user.getName() + "/html/" + exercise.id;
    List<ElementResult<? extends Task>> result = HtmlCorrector.correct(url, exercise, user);
    
    List<String> results = result.stream().map(res -> res.toJSON()).collect(Collectors.toList());
    
    return "{\"results\": [\n\t" + String.join(",\n\t", results) + "\n]}";
  }
  
  private String extractLearnerSolutionFromRequest(Request request, String value) {
    return request.body().asFormUrlEncoded().get(value)[0];
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
