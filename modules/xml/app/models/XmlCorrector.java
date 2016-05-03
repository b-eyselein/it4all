package model.html;

import java.util.List;
import java.util.stream.Collectors;

import model.html.result.ElementResult;
import model.html.task.Task;
import model.user.Student;
import model.user.User;

public class XmlCorrector {
  
  public static List<ElementResult<? extends Task>> correct(String solutionUrl, XmlExercise exercise, User student) {
    WebDriver driver = new HtmlUnitDriver();
    driver.get(solutionUrl);
    
    List<ElementResult<? extends Task>> result = exercise.tasks.stream().map(task -> task.evaluate(driver))
        .collect(Collectors.toList());
    
    return result;
  }
  
}
