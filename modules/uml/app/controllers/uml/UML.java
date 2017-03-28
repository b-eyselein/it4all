package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.ExerciseController;
import model.JsonWrapper;
import model.UmlExercise;
import model.Util;
import model.result.UmlClassselection;
import model.result.UmlDiagramdrawing;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
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
    return ok(views.html.classsel.render(getUser(), UmlExercise.getExercise(exerciseId)));
  }
  
  public Result correctClassSelection(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);

    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, classSelSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = UmlExercise.getExercise(exerciseId);
    UmlClassselection ue = new UmlClassselection(exercise, sentJson);
    
    return ok(views.html.classselsol.render(getUser(), ue));
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);
    
    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, diagDrawSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = UmlExercise.getExercise(exerciseId);
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, sentJson);
    
    return ok(views.html.diagdrawingsol.render(getUser(), ue));
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    DynamicForm form = factory.form().bindFromRequest();
    String jsonAsString = form.get(FORM_VALUE);
    Logger.debug(jsonAsString);
    if(jsonAsString == null || jsonAsString.isEmpty())
      return badRequest(NO_DATA);
    
    JsonNode sentJson = Json.parse(jsonAsString);
    if(!JsonWrapper.validateJson(sentJson, diagDrawSchemaNode))
      return badRequest(CORRUPT_DATA);
    
    UmlExercise exercise = UmlExercise.getExercise(exerciseId);
    UmlDiagramdrawing ue = new UmlDiagramdrawing(exercise, sentJson);
    
    return ok(views.html.diagdrawinghelpsol.render(getUser(), ue));
  }
  
  public Result diagramDrawing(int exerciseId) {
    boolean showHint = false;
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.getExercise(exerciseId), showHint));
  }
  
  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagdrawinghelp.render(getUser(), UmlExercise.getExercise(exerciseId)));
  }
  
  public Result index() {
    return ok(views.html.umloverview.render(Arrays.asList(UmlExercise.getExercise(1), UmlExercise.getExercise(2)),
        getUser()));
  }
  
  public Result test() {
    return ok(views.html.attrmethmatching.render(getUser()));
  }
  
}
