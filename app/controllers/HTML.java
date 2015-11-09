package controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.Exercise;
import model.Student;
import model.html.ElementResult;
import model.html.HtmlCorrector;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.empty;
import views.html.html.htmlcorrect;
import views.html.html.html;
import views.html.html.htmloverview;

public class HTML extends Controller {
  
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
  
  public Result index() {
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    List<Exercise> exercises = Exercise.finder.all();
    return ok(htmloverview.render(exercises, student));
  }
  
  public Result exericse(int exercise) {
    if(session(Application.SESSION_ID_FIELD) == null)
      return redirect("/login");
    // FIXME: kann exericse < 1 sein?
    // FIXME: Student oder Exercise null!
    Student student = Student.find.byId(session(Application.SESSION_ID_FIELD));
    if(student == null)
      return redirect("/login");
    Exercise exer = Exercise.finder.byId(exercise);
    if(exer == null)
      return redirect("/html/");
    return ok(html.render(student, exer));
  }
  
  public Result upload() {
    Student user = Student.find.byId(session(Application.SESSION_ID_FIELD));
    // FIXME: getExercise!
    int exercise = 1;
    Exercise ex = Exercise.finder.byId(exercise);
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart htmlFile = body.getFile("solFile");
    if(htmlFile != null) {
      try {
        Path file = htmlFile.getFile().toPath();
        List<String> fileContent = Files.readAllLines(file);
        saveSolutionForUser(user.name, String.join("\n", fileContent), exercise);
        
        String url = "/solutions/" + user.name + "/html/" + exercise;
        List<ElementResult> result = HtmlCorrector.correct(url, ex, user);
        
        List<String> solution = Files.readAllLines(Util.getSolFileForExercise(user.name, exercise));
        
        return ok(htmlcorrect.render(user, ex, result, solution));
      } catch (IOException e) {
        return badRequest("Datei konnte nicht hochgeladen werden!");
      }
    } else
      return badRequest("Datei konnte nicht hochgeladen werden!");
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
