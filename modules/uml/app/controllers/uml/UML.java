package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject; 

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Util;
import model.UmlExercise;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Result;
import views.html.classselection;
import views.html.diagramdrawing;
import views.html.umloverview;
import views.html.difficulty;

public class UML extends ExerciseController {
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  private static String getExerciseText() {
    try {
      Path file = Paths.get("modules/uml/conf/exerciseText.txt");
      Logger.debug(file.toAbsolutePath().toString());
      return String.join("\n", Files.readAllLines(file));
      
    } catch (IOException e) {
      return "TODO!";
    }
  }
  
  public Result classSelection(int exerciseId) {
    return ok(classselection.render(UserManagement.getCurrentUser()));
  }
  
  public Result diagramDrawing(int exerciseId) {
    return ok(diagramdrawing.render(UserManagement.getCurrentUser(), getExerciseText()));
  }
  
  public Result index() {
    return ok(umloverview.render(Arrays.asList(new UmlExercise()),UserManagement.getCurrentUser()));
  }
  
  public Result diff(int exerciseId){
	  return ok(difficulty.render(UserManagement.getCurrentUser()));
  }
  
  
}
