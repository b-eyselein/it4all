package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import model.Util;
import model.user.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.error;

public class Solution extends Controller {
  
  @Inject
  private Util util;

  public Result site(User user, int exercise) {
    Path file = util.getSolutionFileForExerciseAndType(user, "html", exercise, "html");
    if(!Files.exists(file))
      return badRequest(error.render(user, new Html("Fehler: Datei nicht vorhanden!")));
    try {
      return ok(new Html(String.join("\n", Files.readAllLines(file))));
    } catch (IOException err) {
      Logger.error("Fehler beim Lesen einer Html-Datei: " + file, err);
      return badRequest(error.render(user, new Html("Fehler beim Lesen der Datei!")));
    }

  }
}
