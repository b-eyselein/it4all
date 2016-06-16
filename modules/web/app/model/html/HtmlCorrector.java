package model.html;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.exercise.Grading;
import model.exercise.Grading.GradingKey;
import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.User;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, HtmlExercise exercise, User student, String type) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    List<? extends Task> tasks = Collections.emptyList();
    
    if(type.equals("html"))
      tasks = exercise.tasks;
    else if(type.equals("css"))
      tasks = exercise.cssTasks;
    
    List<ElementResult> result = tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
    
    if(type.equals("html"))
      saveGrading(exercise, student, calculatePoints(result));
    
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
