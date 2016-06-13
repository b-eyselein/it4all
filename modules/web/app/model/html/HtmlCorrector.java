package model.html;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.user.User;
import model.exercise.Grading;
import model.exercise.GradingKey;
import model.html.result.ElementResult;
import model.html.task.CssTask;
import model.html.task.HtmlTask;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    List<HtmlTask> tasks = exercise.tasks;
    List<ElementResult> result = tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
    
    saveGrading(exercise, student, calculatePoints(result));
    
    return result;
  }
  
  public static List<ElementResult> correctCSS(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    List<CssTask> tasks = exercise.cssTasks;
    
    List<ElementResult> result = tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
    
    // TODO: Alternative...
    // saveGrading(exercise, student, calculatePoints(result));
    
    return result;
  }
  
  private static int calculatePoints(List<ElementResult> result) {
    return result.stream().mapToInt(res -> res.getPoints()).sum();
  }
  
  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver();
    driver.get(solutionUrl);
    return driver;
  }
  
  private static void saveGrading(HtmlExercise exercise, User user, int points) {
    GradingKey gradingKey = new GradingKey(user.name, exercise.id);
    
    Grading grading = Grading.finder.byId(gradingKey);
    
    if(grading == null)
      grading = new Grading(gradingKey);
    
    grading.points = points;
    grading.save();
  }
  
}
