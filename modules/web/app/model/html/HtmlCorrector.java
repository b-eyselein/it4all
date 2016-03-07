package model.html;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import model.user.Student;
import model.user.User;

public class HtmlCorrector {
  
  private static final String LOCALHOST = "http://localhost:9000";
  
  private static int calculatePoints(List<ElementResult> result) {
    return result.stream().mapToInt(res -> res.getPoints()).sum();
  }
  
  public static List<ElementResult> correct(String solutionUrl, HtmlExercise exercise, User student) {
    WebDriver driver = getDriverWithUrlAndLoadPage(solutionUrl);
    
    System.out.println(driver.getPageSource());
    System.out.println("******************************");
    
    // TODO: entfernen!
    if(driver.getTitle() == null)
      throw new IllegalArgumentException("Kein Titel!");
    if(!driver.getTitle().equals("Kontaktformular Werkstatt"))
      throw new IllegalArgumentException("Falsche Seite geladen!");
    
    List<ElementResult> result = getElementResultsForExercise(exercise);
    result.stream().forEach(result1 -> result1.evaluate(driver));
    
    int points = calculatePoints(result);
    
    saveGrading(exercise, student, points);
    
    return result;
  }
  
  private static WebDriver getDriverWithUrlAndLoadPage(String solutionUrl) {
    String newUrl = LOCALHOST + solutionUrl;
    WebDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
    // FIXME: what if url does not exist?
    driver.get(newUrl);
    return driver;
  }
  
  private static List<ElementResult> getElementResultsForExercise(HtmlExercise exercise) {
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
