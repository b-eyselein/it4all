package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Http.Request;
import play.mvc.Result;
import views.html.classselection;
import views.html.diagramdrawing;

public class UML extends ExerciseController<IntExerciseIdentifier> {

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
    return ok("TODO!");
  }

  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier exercise) {
    // TODO: implement
    return null;
  }

}
