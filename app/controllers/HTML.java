package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.Exercise;
import model.Student;
import model.html.ElementResult;
import model.html.HtmlCorrector;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.twirl.api.Html;
import views.html.empty;
import views.html.html.htmlcorrect;
import views.html.html.htmlexercise;
import views.html.html.htmlfileupload;
import views.html.html.htmloverview;
import views.html.html.htmlupload;

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
        Path directory = Util.getSolDirForUserAndType("html", userName);
        if(!Files.exists(directory) || !Files.isDirectory(directory))
          Files.createDirectory(directory);
        Path file = Util.getSolFileForExercise(user.name, exerciseNumber);
        if(!Files.exists(file) || !Files.isRegularFile(file)) {
          try {
            Files.write(file, exercise.getDefaultInLines(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
          } catch (IOException e) {
          }
        } else {
          // TODO: Trim all lines, filter out empty lines
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
    Path path = Util.getSolFileForExercise(userName, exercise);
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
  
  public Result htmlOverview() {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    List<Exercise> exercises = Exercise.finder.all();
    return ok(htmloverview.render(exercises, student));
  }
  
  public Result upload() {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    return ok(htmlupload.render(student));
  }
  
  public Result uploadFile(int exerciseID) {
    if(exerciseID == -1)
      return redirect("/index");
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    Exercise exercise = Exercise.finder.byId(exerciseID);
    return ok(htmlfileupload.render(student, exercise));
  }
  
  public Result saveSol() {
//    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
//    Map<String, String[]> asFormUrl = request().body().asFormUrlEncoded();
//    String solution = asFormUrl.get("solution")[0];
//    int exercise = Integer.parseInt(asFormUrl.get("exercise")[0]);
//    saveSolutionForUser(user.name, solution, exercise);
//    
//    String url = "/solutions/" + user.name + "/html/" + exercise;
//    List<ElementResult> result = HtmlCorrector.correct(url, Exercise.finder.byId(exercise));
//    return ok(htmlcorrect.render(user, result, lines));
    return badRequest("Seite sollte nicht aufrufbar sein!");
  }
  
  public Result saveSolFile() {
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    int exercise = 1;
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart htmlFile = body.getFile("solFile");
    if(htmlFile != null) {
      try {
        Path file = htmlFile.getFile().toPath();
        List<String> fileContent = Files.readAllLines(file);
        saveSolutionForUser(user.name, String.join("\n", fileContent), exercise);
        
        String url = "/solutions/" + user.name + "/html/" + exercise;
        List<ElementResult> result = HtmlCorrector.correct(url, Exercise.finder.byId(exercise));
        
        
        List<String> solution = Files.readAllLines(Util.getSolFileForExercise(user.name, exercise));
        
        return ok(htmlcorrect.render(user, result, solution));
      } catch (IOException e) {
        System.out.println(e);
        return badRequest("Datei konnte nicht hochgeladen werden!");
      }
    } else
      return badRequest("Datei konnte nicht hochgeladen werden!");
  }
  
  public WebSocket<String> getWebSocket() {
    // final String user = session(Application.SESSION_ID_FIELD);
    return WebSocket.whenReady((in, out) -> {
      in.onMessage(solution -> {
        // saveSolutionForUser(user, solution, exercise);
        // String url = "/solutions/" + user + "/html/" + exercise;
        // Exercise exerciseToCorrect = Exercise.finder.byId(exercise);
        // List<ElementResult> result = HtmlCorrector.correct(url,
        // exerciseToCorrect);
        // List<String> strings = result.stream().map(res ->
        // res.toString()).collect(Collectors.toList());
        // out.write(String.join("\n", strings));
        out.write("Solution: " + solution);
      });
      
      in.onClose(() -> {
      });
      
      out.write("Hallo neuer Client...");
    });
  }
  
  private void saveSolutionForUser(final String user, String solution, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(user)))
          Files.createDirectory(Util.getSolDirForUser(user));
      
      Path solDir = Util.getSolDirForUserAndType("html", user);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);
      
      Path saveTo = Util.getSolFileForExercise(user, exercise);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
