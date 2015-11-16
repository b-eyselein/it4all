package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.Exercise;
import model.Grading;
import model.user.Student;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {
  
  public static List<ElementResult> correct(String solutionUrl, Exercise exercise, Student student) {
    String newUrl = "http://localhost:9000/" + solutionUrl;
    WebDriver driver = new HtmlUnitDriver();
    driver.get(newUrl);
    
    List<ElementResult> result = exercise.tasks.parallelStream().map(task -> task.getElementResult())
        .collect(Collectors.toList());
    result.parallelStream().forEach(result1 -> result1.evaluate(driver));
    
    int points = result.stream().mapToInt(res -> res.getPoints()).sum();
    
    Grading grading = new Grading();
    grading.student = student;
    grading.exercise = exercise;
    grading.points = points;
    grading.save();
    
    return result;
  }
  
}
