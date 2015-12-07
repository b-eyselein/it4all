package model.html;

import java.util.List;

import model.Exercise;
import model.Grading;
import model.user.Student;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {
  
  private static final String LOCALHOST = "http://localhost:9000/";
  
  public static List<ElementResult> correct(String solutionUrl, Exercise exercise, Student student) {
    WebDriver driver = getDriverWithUrlAndLoadPage(solutionUrl);
    
    List<ElementResult> result = getElementResultsForExercise(exercise);
    result.parallelStream().forEach(result1 -> result1.evaluate(driver));
    
    int points = result.stream().mapToInt(res -> res.getPoints()).sum();
    
    // TODO: override old Grading?
    Grading grading = new Grading();
    grading.student = student;
    grading.exercise = exercise;
    grading.points = points;
    grading.save();
    
    return result;
  }
  
  private static WebDriver getDriverWithUrlAndLoadPage(String solutionUrl) {
    String newUrl = LOCALHOST + solutionUrl;
    WebDriver driver = new HtmlUnitDriver();
    driver.get(newUrl);
    return driver;
  }
  
  private static List<ElementResult> getElementResultsForExercise(Exercise exercise) {
    return exercise.getElementResults();
  }
  
}
