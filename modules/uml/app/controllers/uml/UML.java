package controllers.uml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.inject.Inject;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.JValidator;
import model.UmlClassselection;
import model.UmlDiagramdrawing;
import model.UmlExercise;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.Request;
import play.mvc.Result;

public class UML extends ExerciseController {

  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  private static String getExerciseText() {
    try {
      Path file = Paths.get("modules/uml/conf/exerciseText.txt");
      // Logger.debug(file.toAbsolutePath().toString());
      return String.join("\n", Files.readAllLines(file));

    } catch (IOException e) {
      return "TODO!";
    }
  }

  public static File getSchema_Classselection() {
    Path path = Paths.get("modules/uml/conf/schema_classselection.json");
    File file = new File(path.toAbsolutePath().toString());
    return file;
  }

  private static File getSchema_Diagramdrawing() {
    Path path = Paths.get("modules/uml/conf/schema_diagrammdrawing.json");
    File file = new File(path.toAbsolutePath().toString());
    return file;
  }

  public Result classSelection(int exerciseId) {
    return ok(views.html.classselection.render(UserManagement.getCurrentUser()));
  }

  @Override
  protected CompleteResult correct(Request request, User user, int exerciseId) {
    // TODO Auto-generated method stub
    return null;
  }

  public Result correctclassselection() {
    DynamicForm form = factory.form().bindFromRequest();
    String classes = form.get("fname");

    if(classes == null || classes.isEmpty())
      return badRequest("Keine Daten übertragen!");

    UmlClassselection ue = new UmlClassselection(classes);
    return ok(views.html.solution_classselection.render(UserManagement.getCurrentUser(), ue));
  }

  public Result correctdiagramdrawing() throws IOException {
    DynamicForm form = factory.form().bindFromRequest();
    String json = form.get("fname");
    Logger.debug("diagramdrawhinghelp(): " + json);
    if(json == null || json.isEmpty())
      return badRequest("Keine Daten übertragen!");
    try {
      if(!JValidator.validateJson(getSchema_Diagramdrawing(), new File(json))) {
        return badRequest("Die gesendeten Daten sind fehlerhaft!");
      }
    } catch (ProcessingException e) {
      return badRequest("Die gesendeten Daten sind fehlerhaft!");
    }
    UmlDiagramdrawing ue = new UmlDiagramdrawing(json);
    return ok(views.html.solution_diagramdrawing.render(UserManagement.getCurrentUser(), ue));
  }

  public Result correctdiagramdrawinghelp() throws IOException {
    DynamicForm form = factory.form().bindFromRequest();
    String json = form.get("fname");
    if(json == null || json.isEmpty())
      return badRequest("Keine Daten übertragen!");
    ;
    UmlDiagramdrawing ue = new UmlDiagramdrawing(json);
    return ok(views.html.solution_diagramdrawinghelp.render(UserManagement.getCurrentUser(), ue));
  }

  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagramdrawing.render(UserManagement.getCurrentUser(), getExerciseText()));
  }

  public Result diagramDrawingHelp(int exerciseId) {
    return ok(views.html.diagramdrawinghelp.render(UserManagement.getCurrentUser()));
  }

  public Result diff(int exerciseId) {
    return ok(views.html.difficulty.render(UserManagement.getCurrentUser()));
  }

  public Result index() {
    return ok(views.html.umloverview.render(Arrays.asList(new UmlExercise(1)), UserManagement.getCurrentUser()));
  }

}
