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
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class UML extends ExerciseController {
  
  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path SOLUTION_SCHEMA_PATH = Paths.get(BASE_PATH.toString(), "solutionSchema.json");
  
  private static final String ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!";
  
  private JsonNode solutionSchemaNode;
  
  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
    
    try {
      solutionSchemaNode = Json.parse(String.join("\n", Files.readAllLines(SOLUTION_SCHEMA_PATH)));
    } catch (IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
    }
  }
  
  private static Optional<ProcessingReport> validateSolution(JsonNode sentJson, JsonNode schemaNode) {
    return JsonWrapper.validateJson(sentJson, schemaNode);
  }
  
  public Result classSelection(int exerciseId) {
    return ok(views.html.classSelection.render(getUser(), UmlExercise.finder.byId(exerciseId)));
  }
  
  public Result correctClassSelection(int exerciseId) {
    try {
      ClassSelectionResult result = new ClassSelectionResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.classSelectionSolution.render(getUser(), result));
    } catch (Exception e) {
      return badRequest(e.getMessage());
    }
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    try {
      DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.diagdrawingsol.render(getUser(), result));
      
    } catch (Exception e) {
      return badRequest(e.getMessage());
    }
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    try {
      DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.diagdrawinghelpsol.render(getUser(), result));
    } catch (Exception e) {
      return badRequest(e.getMessage());
    }
  }
  
  public Result correctMatching(int exerciseId) {
    return ok(views.html.matchingCorrection.render(getUser()));
  }
  
  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.finder.byId(exerciseId), false));
  }
  
  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.finder.byId(exerciseId), true));
  }
  
  public Result index() {
    return ok(views.html.umloverview.render(getUser(), UmlExercise.finder.all()));
  }
  
  public Result matching(int exerciseId) {
    UmlExercise exercise = UmlExercise.finder.byId(exerciseId);
    return ok(views.html.matching.render(getUser(), exercise, exercise.getSolution()));
  }
  
  // FIXME: Type of Exception!
  private UmlSolution readSolutionFromForm() throws Exception {
    JsonNode sentJson = Json.parse(factory.form().bindFromRequest().get(LEARNER_SOLUTION_VALUE));
    
    Logger.debug(Json.prettyPrint(sentJson));
    
    Optional<ProcessingReport> report = validateSolution(sentJson, solutionSchemaNode);
    
    if(!report.isPresent())
      throw new Exception(ERROR_MSG);
    
    if(!report.get().isSuccess())
      throw new Exception(report.get().toString());
    
    return Json.fromJson(sentJson, UmlSolution.class);
  }
  
}
