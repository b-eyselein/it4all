package model.html;

import java.util.Collections;
import java.util.List;

import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Student;
import model.user.User;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector {

  private static final String LOCALHOST = "http://localhost:9000";

  public static List<ElementResult<? extends Task>> correct(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = getDriverWithUrlAndLoadPage(solutionUrl);

    List<ElementResult<? extends Task>> result = getElementResultsForExercise(exercise);
    result.stream().forEach(result1 -> result1.evaluate(driver));

    int points = calculatePoints(result);

    saveGrading(exercise, student, points);

    return result;
  }

  private static int calculatePoints(List<ElementResult<? extends Task>> result) {
    return result.stream().mapToInt(res -> res.getPoints()).sum();
  }

  private static WebDriver getDriverWithUrlAndLoadPage(String solutionUrl) {
    String newUrl = LOCALHOST + solutionUrl;
    WebDriver driver = new HtmlUnitDriver();
    // FIXME: what if url does not exist?
    driver.get(newUrl);
    return driver;
  }

  private static List<ElementResult<? extends Task>> getElementResultsForExercise(HtmlExercise exercise) {
    return exercise.getElementResults();
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
