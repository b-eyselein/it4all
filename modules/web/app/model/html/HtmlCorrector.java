package model.html;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import model.user.User;
import model.exercise.Grading;
import model.exercise.GradingKey;
import model.html.result.ElementResult;

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

  private static void saveGrading(HtmlExercise exercise, User user, int points) {
    GradingKey gradingKey = new GradingKey(user.name, exercise.id);

    Grading grading = Grading.finder.byId(gradingKey);

    if(grading == null)
      grading = new Grading(gradingKey);
    
    grading.points = points;
    grading.save();
  }

}
