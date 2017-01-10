package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.UmlExercise;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
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
  
  public Result classSelection(int id) {
    return ok(classselection.render(UserManagement.getCurrentUser()));
  }
  
  public Result correct() {
    // Correct classes...
    DynamicForm form = factory.form().bindFromRequest();
    String classes = form.get("fname");
    JsonNode node = Json.parse(classes);
    node.get("classes");
    Logger.debug(Json.prettyPrint(node));
    
    return ok("HALLO!");
  }

  @Override
  protected CompleteResult correct(Request request, User user, int id) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Result diagramDrawing(int id) {
    return ok(diagramdrawing.render(UserManagement.getCurrentUser(), getExerciseText()));
  }
  
  public Result diff(int id) {
    return ok(difficulty.render(UserManagement.getCurrentUser()));
  }
  
  public Result index() {
    return ok(
        umloverview.render(Arrays.asList(new UmlExercise(1), new UmlExercise(2)), UserManagement.getCurrentUser()));
  }

}
