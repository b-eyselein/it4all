package controllers.uml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.inject.Inject;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.JValidator;
import model.UmlExercise;
import model.Util;
import model.result.CompleteResult;
import model.result.UmlClassselection;
import model.result.UmlDiagramdrawing;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.Request;
import play.mvc.Result;

public class UML extends ExerciseController {
  
  private static final String FORM_VALUE = "fname";
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public static File getSchema_Classselection() {
    Path path = Paths.get("modules/uml/conf/schema_classselection.json");
    File file = new File(path.toAbsolutePath().toString());
    return file;
  }
  
  private static File getSchema_DiagramDrawing() {
    Path path = Paths.get("modules/uml/conf/schema_diagrammdrawing.json");
    File file = new File(path.toAbsolutePath().toString());
    return file;
  }
  
  public Result classSelection(int exerciseId) {
    return ok(views.html.classselection.render(UserManagement.getCurrentUser(), new UmlExercise(exerciseId)));
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, int exerciseId) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public Result correctClassSelection(int exerciseId) {
    UmlExercise exercise = new UmlExercise(exerciseId);
    
    DynamicForm form = factory.form().bindFromRequest();
    String classes = form.get(FORM_VALUE);
    
    if(classes == null || classes.isEmpty())
      return badRequest("Keine Daten übertragen!");
    
    UmlClassselection ue = new UmlClassselection(exercise, classes);
    return ok(views.html.solution_classselection.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    UmlExercise exercise = new UmlExercise(exerciseId);
    
    DynamicForm form = factory.form().bindFromRequest();
    String json = form.get(FORM_VALUE);
    Logger.debug("diagramdrawhinghelp(): " + json);
    
    if(json == null || json.isEmpty())
      return badRequest("Keine Daten übertragen!");
    
    try {
      if(!JValidator.validateJson(getSchema_DiagramDrawing(), new File(json))) {
        return badRequest("Die gesendeten Daten sind fehlerhaft!");
      }
    } catch (ProcessingException | IOException e) {
      return badRequest("Die gesendeten Daten sind fehlerhaft!");
    }
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, json);
    return ok(views.html.solution_diagramdrawing.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    UmlExercise exercise = new UmlExercise(exerciseId);
    
    DynamicForm form = factory.form().bindFromRequest();
    String json = form.get(FORM_VALUE);

    if(json == null || json.isEmpty())
      return badRequest("Keine Daten übertragen!");
    
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, json);
    return ok(views.html.solution_diagramdrawinghelp.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagramdrawing.render(UserManagement.getCurrentUser(), new UmlExercise(exerciseId)));
  }
  
  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagramdrawinghelp.render(UserManagement.getCurrentUser(), new UmlExercise(exerciseId)));
  }
  
  public Result diff(int exerciseId) {
    return ok(views.html.difficulty.render(UserManagement.getCurrentUser()));
  }
  
  public Result index() {
    return ok(views.html.umloverview.render(Arrays.asList(new UmlExercise(1)), UserManagement.getCurrentUser()));
  }
  
}
