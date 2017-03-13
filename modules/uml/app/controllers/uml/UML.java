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
import model.JsonWrapper;
import model.UmlExercise;
import model.Util;
import model.result.CompleteResult;
import model.result.UmlClassselection;
import model.result.UmlDiagramdrawing;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;

public class UML extends ExerciseController {
  
  private static final String FORM_VALUE = "learnerSolution";
  
  private static final String NO_DATA = "Keine Daten übertragen!";
  private static final String CORRUPT_DATA = "Die gesendeten Daten sind fehlerhaft!";
  
  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path CLASS_SELECTION_SCHEMA_PATH = Paths.get(BASE_PATH.toString(), "classSelSchema.json");
  private static final Path DIAGRAM_DRAWING_SCHEMA_PATH = Paths.get(BASE_PATH.toString(), "diagDrawingSchema.json");
  
  private static JsonNode classSelSchemaNode;
  private static JsonNode diagDrawSchemaNode;
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
    
    try {
      classSelSchemaNode = Json.parse(String.join("\n", Files.readAllLines(CLASS_SELECTION_SCHEMA_PATH)));
      diagDrawSchemaNode = Json.parse(String.join("\n", Files.readAllLines(DIAGRAM_DRAWING_SCHEMA_PATH)));
    } catch (IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
    }
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
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);
    
    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, classSelSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = new UmlExercise(exerciseId);
    UmlClassselection ue = new UmlClassselection(exercise, sentJson);
    
    return ok(views.html.solution_classselection.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);
    
    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, diagDrawSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = new UmlExercise(exerciseId);
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, sentJson);
    
    return ok(views.html.solution_diagramdrawing.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);
    
    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, diagDrawSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = new UmlExercise(exerciseId);
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, sentJson);
    
    return ok(views.html.solution_diagramdrawinghelp.render(UserManagement.getCurrentUser(), ue));
  }
  
  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagramdrawing.render(UserManagement.getCurrentUser(), new UmlExercise(exerciseId)));
  }
  
  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagramdrawinghelp.render(UserManagement.getCurrentUser(), new UmlExercise(exerciseId)));
  }
  
  public Result index() {
    return ok(views.html.umloverview.render(Arrays.asList(new UmlExercise(1)), UserManagement.getCurrentUser()));
  }
  
}
