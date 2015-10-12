package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.Exercise;
import model.Student;
import model.html.HtmlCorrector;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.twirl.api.Html;
import views.html.empty;
import views.html.htmlexercise;
import views.html.htmloverview;

public class HTML extends Controller {
  
  public Result html(int exercise) {
    String userName = session("id");
    if(userName == null)
      return redirect("/login");
    Student user = Student.find.byId(userName);
    
    if(exercise == -1 || Exercise.finder.byId(exercise) == null)
      return ok(htmloverview.render(Exercise.finder.all(), user));
    else
      return ok(htmlexercise.render(Exercise.finder.byId(exercise), user));
  }
  
  public Result site(String snr) {
    List<String> strings;
    try {
      Path path = Paths.get("solutions/html/" + snr + "/file.html");
      strings = Files.readAllLines(path);
    } catch (IOException e) {
      strings = Arrays.asList("Es gab einen Fehler...");
    }
    return ok(empty.render(new Html(String.join("\n", strings))));
  }
  
  public WebSocket<String> getWebSocket() {
    final String user = session("id");
    return WebSocket.whenReady((in, out) -> {
      in.onMessage(solution -> {
        saveSolutionForUser(user, solution);
        // TODO: correct solution
        HtmlCorrector corrector = new HtmlCorrector();
        String url = "/html/solution/" + user;
        out.write(String.join("\n", corrector.correct(url)));
      });
      
      in.onClose(() -> {
        // TODO: save current solution (already saved?)
      });
    });
  }
  
  private void saveSolutionForUser(final String user, String solution) {
    try {
      Path directory = Paths.get("solutions/html/" + user);
      if(!Files.exists(directory))
        Files.createDirectory(directory);
      Path path = Paths.get(directory.toString(), "/file.html");
      Files.write(path, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      // TODO: Konnte Loesung nicht speichern!
    }
  }
}
