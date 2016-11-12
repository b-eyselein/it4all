package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.UmlExercise;
import model.Util;
import model.result.EvaluationResult;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Result;
import views.html.classselection;
import views.html.diagramdrawing;

public class UML extends ExerciseController<UmlExercise> {

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

  protected EvaluationResult correct(String learnerSolution, UmlExercise exercise) {
    // TODO: implement
    return null;
  }

}
