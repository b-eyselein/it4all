package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.ExerciseController;
import model.JsonWrapper;
import model.SolutionException;
import model.UmlExercise;
import model.UmlSolution;
import model.Util;
import model.result.ClassSelectionResult;
import model.result.UmlDiagramdrawing;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class UML extends ExerciseController {

  private static final Path BASE_PATH = Paths.get("modules", "uml", "conf", "resources");
  private static final Path SOLUTION_SCHEMA_PATH = Paths.get(BASE_PATH.toString(), "solutionSchema.json");

  private static JsonNode solutionSchemaNode;

  @Inject
  public UML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);

    try {
      solutionSchemaNode = Json.parse(String.join("\n", Files.readAllLines(SOLUTION_SCHEMA_PATH)));
    } catch (IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
    }
  }

  private static JsonNode readAndValidateSolution(DynamicForm form, JsonNode schemaNode) throws SolutionException {
    String jsonAsString = form.get(LEARNER_SOLUTION_VALUE);
    
    if(jsonAsString == null || jsonAsString.isEmpty())
      throw new SolutionException("Keine Daten übertragen!");
    
    JsonNode sentJson = Json.parse(jsonAsString);
    try {
      ProcessingReport report = JsonWrapper.validateJson(sentJson, schemaNode);
      if(!report.isSuccess())
        throw new SolutionException("Gesendete Daten entsprechen nicht dem Schema:\n" + report.toString());
      
    } catch (ProcessingException e) {
      Logger.debug("There has been an error correcting an UML class diagram", e);
      throw new SolutionException("Die gesendeten Daten sind fehlerhaft!");
    }
    
    return sentJson;
  }

  public Result classSelection(int exerciseId) {
    return ok(views.html.classSelection.render(getUser(), UmlExercise.finder.byId(exerciseId)));
  }

  public Result correctClassSelection(int exerciseId) {
    try {
      JsonNode sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
      ClassSelectionResult result = new ClassSelectionResult(UmlExercise.finder.byId(exerciseId), sentJson);
      return ok(views.html.classSelectionSolution.render(getUser(), result));
    } catch (SolutionException e) {
      return badRequest(e.getMessage());
    }
  }

  public Result correctDiagramDrawing(int exerciseId) {
    try {
      JsonNode sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
      UmlDiagramdrawing result = new UmlDiagramdrawing(UmlExercise.finder.byId(exerciseId), sentJson);
      return ok(views.html.diagdrawingsol.render(getUser(), result));
    } catch (SolutionException e) {
      return badRequest(e.getMessage());
    }
  }

  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    try {
      JsonNode sentJson = readAndValidateSolution(factory.form().bindFromRequest(), solutionSchemaNode);
      UmlDiagramdrawing result = new UmlDiagramdrawing(UmlExercise.finder.byId(exerciseId), sentJson);
      return ok(views.html.diagdrawinghelpsol.render(getUser(), result));
    } catch (SolutionException e) {
      return badRequest(e.getMessage());
    }
  }

  public Result diagramDrawing(int exerciseId) {
    boolean showHint = false;
    return ok(views.html.diagdrawing.render(getUser(), UmlExercise.finder.byId(exerciseId), showHint));
  }

  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagdrawinghelp.render(getUser(), UmlExercise.finder.byId(exerciseId)));
  }

  public Result index() {
    return ok(views.html.umloverview.render(UmlExercise.finder.all(), getUser()));
  }

  public Result test() {
    return ok(views.html.attrmethmatching.render(getUser(), UmlExercise.finder.byId(2), new UmlSolution()));
  }

}
