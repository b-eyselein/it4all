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

  private static final String FILE_TYPE = "html";

  // @Inject
  private Util util;

  @Inject
  public Solution(Util theUtil) {
    util = theUtil;
  }

  public Result site(User user, String type, int exerciseId) {
    Path file = util.getSolFileForExercise(user, type, exerciseId, FILE_TYPE);

    if(!file.toFile().exists())
      return badRequest(error.render(user, "Fehler: Datei nicht vorhanden!"));

    try {
      return ok(new Html(String.join("\n", Files.readAllLines(file))));
    } catch (IOException err) {
      Logger.error("Fehler beim Lesen einer Html-Datei: " + file, err);
      return badRequest(error.render(user, "Fehler beim Lesen der Datei!"));
    }
  }

}
