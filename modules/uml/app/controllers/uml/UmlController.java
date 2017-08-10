package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.StringConsts;
import model.UmlExercise;
import model.UmlSolution;
import model.exercisereading.ExerciseReader;
import model.result.ClassSelectionResult;
import model.result.DiagramDrawingResult;
import model.result.UmlResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class UmlController extends ExerciseController<UmlExercise, UmlResult> {
  
  private static final Path SOLUTION_SCHEMA_PATH = Paths.get("conf", "resources", "uml", "solutionSchema.json");
  
  private static final String ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!";
  
  public static final JsonNode SOLUTION_SCHEMA_NODE = initSolutionSchemaNode();
  
  @Inject
  public UmlController(FormFactory theFactory) {
    super(theFactory, "uml", UmlExercise.finder);
  }
  
  private static JsonNode initSolutionSchemaNode() {
    try {
      return Json.parse(String.join("\n", Files.readAllLines(SOLUTION_SCHEMA_PATH)));
    } catch (IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
      return null;
    }
  }
  
  public Result classSelection(int exerciseId) {
    return ok(views.html.classSelection.render(getUser(), UmlExercise.finder.byId(exerciseId)));
  }
  
  public Result correctClassSelection(int exerciseId) {
    try {
      ClassSelectionResult result = new ClassSelectionResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.classSelectionSolution.render(getUser(), result));
    } catch (CorrectionException e) {
      Logger.error(ERROR_MSG, e);
      return badRequest(e.getMessage());
    }
  }
  
  public Result correctDiagramDrawing(int exerciseId) {
    try {
      DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.diagdrawingsol.render(getUser(), result));
      
    } catch (CorrectionException e) {
      Logger.error(ERROR_MSG, e);
      return badRequest(e.getMessage());
    }
  }
  
  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    try {
      DiagramDrawingResult result = new DiagramDrawingResult(UmlExercise.finder.byId(exerciseId),
          readSolutionFromForm());
      
      return ok(views.html.diagdrawinghelpsol.render(getUser(), result));
    } catch (CorrectionException e) {
      Logger.error(ERROR_MSG, e);
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
  
  private UmlSolution readSolutionFromForm() throws CorrectionException {
    JsonNode sentJson = Json.parse(factory.form().bindFromRequest().get(StringConsts.FORM_VALUE));
    try {
      ProcessingReport report = ExerciseReader.validateJson(sentJson, SOLUTION_SCHEMA_NODE);
      if(report.isSuccess())
        return Json.fromJson(sentJson, UmlSolution.class);
      
      throw new CorrectionException(Json.prettyPrint(sentJson), report.toString());
    } catch (ProcessingException e) {
      throw new CorrectionException(Json.prettyPrint(sentJson), "TODO!");
    }
  }
  
  @Override
  protected List<UmlResult> correct(String learnerSolution, UmlExercise exercise, User user) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Html renderResult(List<UmlResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
