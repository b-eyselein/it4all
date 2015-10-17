package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.Exercise;
import model.Student;
import model.html.ElementResult;
import model.html.HtmlCorrector;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.twirl.api.Html;
import views.html.empty;
import views.html.html.htmlexercise;
import views.html.html.htmloverview;

public class HTML extends Controller {
  
  public Result html(int exerciseNumber) {
    String userName = session("id");
    if(userName == null)
      return redirect("/login");
    Student user = Student.find.byId(userName);
    
    if(exerciseNumber == -1 || Exercise.finder.byId(exerciseNumber) == null)
      return ok(htmloverview.render(Exercise.finder.all(), user));
    else {
      Exercise exercise = Exercise.finder.byId(exerciseNumber);
      List<String> defaultSolution = exercise.getDefaultInLines();
      try {
        Path directory = Paths.get("solutions/" + userName + "/html/");
        if(!Files.exists(directory) || !Files.isDirectory(directory)) {
          Files.createDirectory(directory);
        }
        Path file = Paths.get(directory.toString(), exerciseNumber + ".html");
        if(!Files.exists(file) || !Files.isRegularFile(file)) {
          try {
            Files.write(file, exercise.getDefaultInLines(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
          } catch (IOException e) {
          }
        } else {
          // FIXME: Trim all lines, filter out empty lines
          defaultSolution = Files.readAllLines(file).stream().map(line -> line.trim()).filter(line -> !line.isEmpty())
              .collect(Collectors.toList());
        }
      } catch (IOException e) {
      }
      return ok(htmlexercise.render(exercise, user, defaultSolution));
    }
  }
  
  public Result site(String userName, int exercise) {
    List<String> strings = Arrays.asList("Es gab einen Fehler...");
    Path path = Paths.get("solutions/" + userName + "/html/" + exercise + ".html");
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
  
  public WebSocket<String> getWebSocket(int exercise) {
    final String user = session(Application.SESSION_ID_FIELD);
    return WebSocket.whenReady((in, out) -> {
      in.onMessage(solution -> {
        saveSolutionForUser(user, solution, exercise);
        String url = "/solutions/" + user + "/html/" + exercise;
        Exercise exerciseToCorrect = Exercise.finder.byId(exercise);
        List<ElementResult> result = HtmlCorrector.correct(url, exerciseToCorrect);
        List<String> strings = result.stream().map(res -> res.toString()).collect(Collectors.toList());
        out.write(String.join("\n", strings));
      });
      
      in.onClose(() -> {
      });
    });
  }
  
  private void saveSolutionForUser(final String user, String solution, int exercise) {
    try {
      Path file = Paths.get("solutions/" + user + "/html/" + exercise + ".html");
      Files.write(file, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
    }
  }
}
