package model.html;

import java.util.Arrays;
import java.util.List;

import model.Corrector;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class HtmlCorrector extends Corrector<HtmlExercise> {
  
  public HtmlCorrector(HtmlExercise exercise) {
    super(exercise);
  }
  
  public HtmlCorrector() {
    // TODO: Fix, falls nur eine Aufgabe!!!!!
    super(HtmlExercise.exerciseFinder.byId(1));
  }
  
  @Override
  public List<String> correct(String url) {
    // FIXME: correct with Selenium!!!!
    WebDriver driver = new HtmlUnitDriver();
    String newUrl = "http://localhost:9000" + url;
    driver.get(newUrl);
    try {
      WebElement paragraph = driver.findElement(By.tagName("p"));
      if(paragraph != null)
        System.out.println("Paragraph gefunden!");
      else
        System.out.println("Keinen Paragraph gefunden...");
    } catch (NoSuchElementException e) {
      System.out.println("Keinen Paragraph gefunden...");
    }
    return Arrays.asList("Keine Fehler");
  }
  
}
