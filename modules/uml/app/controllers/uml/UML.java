package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.ExerciseController;
import model.JsonWrapper;
import model.UmlExercise;
import model.UmlSolution;
import model.Util;
import model.result.ClassSelectionResult;
import model.result.DiagramDrawingResult;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class UML extends ExerciseController {
  
  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path SOLUTION_SCHEMA_PATH = Paths.get(BASE_PATH.toString(), "solutionSchema.json");
  
  private static final String ERROR_MSG = "Es gab einen Fehler beim Übertragen des Resultats!";
  
  private static JsonNode solutionSchemaNode;
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
    
    try {
      solutionSchemaNode = Json.parse(String.join("\n", Files.readAllLines(SOLUTION_SCHEMA_PATH))); // NOSONAR
    } catch (IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
    }
  }
  
  private static Optional<JsonNode> readAndValidateSolution(DynamicForm form, JsonNode schemaNode) {
    Optional<String> jsonAsString = Optional.of(form.get(LEARNER_SOLUTION_VALUE));
    
    if(!jsonAsString.isPresent())
      return Optional.empty();
    
    Logger.debug(">>" + jsonAsString.toString() + "<<");
    
    JsonNode sentJson = Json.parse(jsonAsString.get());
    
    Optional<ProcessingReport> report = JsonWrapper.validateJson(sentJson, schemaNode);
    return (report.isPresent() && report.get().isSuccess()) ? Optional.of(sentJson) : Optional.empty();
  }
  
  public Result classSelection(int exerciseId) {
    return ok(views.html.classSelection.render(getUser(), UmlExercise.finder.byId(exerciseId)));
  }
  
  public Result correctClassSelection(int exerciseId) {
    Optional<JsonNode> sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
    
    if(!sentJson.isPresent())
      return badRequest(ERROR_MSG);
    
    ClassSelectionResult result = new ClassSelectionResult(UmlExercise.finder.byId(exerciseId), sentJson.get());
    return ok(views.html.classSelectionSolution.render(getUser(), result));
    
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    Optional<JsonNode> sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
    
    if(!sentJson.isPresent())
      return badRequest(ERROR_MSG);
    
    DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId), sentJson.get());
    return ok(views.html.diagdrawingsol.render(getUser(), result));
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    Optional<JsonNode> sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
    
    if(!sentJson.isPresent())
      return badRequest(ERROR_MSG);
    
    DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId), sentJson.get());
    return ok(views.html.diagdrawinghelpsol.render(getUser(), result));
  }
  
  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.finder.byId(exerciseId), false));
  }
  
  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.finder.byId(exerciseId), true));
  }
  
  public Result index() {
    return ok(views.html.umloverview.render(UmlExercise.finder.all(), getUser()));
  }
  
  public Result test() {
    return ok(views.html.attrmethmatching.render(getUser(), UmlExercise.finder.byId(2), new UmlSolution()));
  }
  
}
