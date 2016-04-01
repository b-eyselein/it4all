package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Student;
import model.user.User;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {
  
  public static List<ElementResult<? extends Task>> correct(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = new HtmlUnitDriver();
    driver.get(solutionUrl);
    
    List<ElementResult<? extends Task>> result = exercise.tasks.stream().map(task -> task.evaluate(driver))
        .collect(Collectors.toList());
    
    int points = calculatePoints(result);
    
    saveGrading(exercise, student, points);
    
    return result;
  }
  
  private static int calculatePoints(List<ElementResult<? extends Task>> result) {
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
