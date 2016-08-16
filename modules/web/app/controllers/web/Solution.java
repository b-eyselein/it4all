package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import model.Util;
import model.javascript.web.JsWebExercise;
import model.user.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.error;

public class Solution extends Controller {
  
  private static final String FILE_TYPE = "html";
  
  @Inject
  private Util util;
  
  public Result site(User user, String type, int exerciseId) {
    Path file = util.getSolFileForExercise(user, type, exerciseId, FILE_TYPE);
    
    if(!Files.exists(file))
      return badRequest(error.render(user, new Html("Fehler: Datei nicht vorhanden!")));
    
    try {
      
      if(type.equals("js")) {
        JsWebExercise exercise = JsWebExercise.finder.byId(exerciseId);
        String site = exercise.anterior + "\n" + String.join("\n", Files.readAllLines(file)) + "\n"
            + exercise.posterior;
        return ok(new Html(site));
      }
      
      return ok(new Html(String.join("\n", Files.readAllLines(file))));
    } catch (IOException err) {
      Logger.error("Fehler beim Lesen einer Html-Datei: " + file, err);
      return badRequest(error.render(user, new Html("Fehler beim Lesen der Datei!")));
    }
  }
  
}
