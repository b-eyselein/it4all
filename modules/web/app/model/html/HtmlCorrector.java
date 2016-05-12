package model.html;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.html.result.ElementResult;
import model.user.Student;
import model.user.User;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = new HtmlUnitDriver();
    driver.get(solutionUrl);
    
    List<ElementResult> result = exercise.tasks.stream().map(task -> task.evaluate(driver))
        .collect(Collectors.toList());
    
    int points = calculatePoints(result);
    
    saveGrading(exercise, student, points);
    
    return result;
  }
  
  private static int calculatePoints(List<ElementResult> result) {
    return result.stream().mapToInt(res -> res.getPoints()).sum();
  }
  
  private static void saveGrading(HtmlExercise exercise, User student, int points) {
    // TODO: override old Grading?
    Grading grading = new Grading();
    // TODO: Casting!
    grading.student = (Student) student;
    grading.exercise = exercise;
    grading.points = points;
    grading.save();
  }
  
}
