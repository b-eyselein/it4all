package model.html;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.exercise.Grading;
import model.exercise.Grading.GradingKey;
import model.html.result.WebCorrectionResult;
import model.html.task.Task;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;

public class HtmlCorrector {
  
  private HtmlCorrector() {
    
  }
  
  public static CompleteResult correct(String learnerSolution, String solutionUrl, WebExercise exercise, User student,
      String type) {
    WebDriver driver = loadWebSite(solutionUrl);
    
    List<? extends Task> tasks = exercise.getTasks(type);
    
    List<EvaluationResult> results = tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
    
    if("html".equals(type))
      saveGrading(exercise, student, calculatePoints(results));
    
    return new WebCorrectionResult(learnerSolution, results);
  }
  
  private static int calculatePoints(List<EvaluationResult> result) {
    return result.stream().mapToInt(res -> res.getPoints()).sum();
  }

  private static WebDriver loadWebSite(String solutionUrl) {
    WebDriver driver = new HtmlUnitDriver();
    driver.get(solutionUrl);
    return driver;
  }
  
  private static void saveGrading(WebExercise exercise, User user, int points) {
    GradingKey gradingKey = new GradingKey(user.name, exercise.id);
    Grading grading = Grading.finder.byId(gradingKey);
    
    if(grading == null)
      grading = new Grading(gradingKey);
    
    grading.setPoints(points);
    grading.save();
  }
  
}
