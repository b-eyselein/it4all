package controllers.uml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.ExerciseController;
import model.ClassSelectionResult;
import model.CorrectionException;
import model.DiagramDrawingResult;
import model.UmlExercise;
import model.UmlResult;
import model.UmlSolution;
import model.UmlSolution$;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class UmlController extends ExerciseController<UmlExercise, UmlResult> {

  private static final Path SOLUTION_SCHEMA_PATH = Paths.get("conf", "resources", "uml", "solutionSchema.json");

  private static final String ERROR_MSG = "Es gab einen Fehler bei der Validierung des Resultats!";

  public static final JsonNode SOLUTION_SCHEMA_NODE = initSolutionSchemaNode();

  private static final Html EX_LIST_REST = new Html("<div class=\"alert alert-info\">"
      + "Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, "
      + "die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.</div>\n<hr>");

  @Inject
  public UmlController(FormFactory theFactory) {
    super(theFactory, "uml", UmlExercise.finder, UmlToolObject$.MODULE$);
  }

  private static JsonNode initSolutionSchemaNode() {
    try {
      return Json.parse(String.join("\n", Files.readAllLines(SOLUTION_SCHEMA_PATH)));
    } catch (final IOException e) {
      Logger.error("There has been an error parsing the schema files for UML:", e);
      return null;
    }
  }

  public Result classSelection(int exerciseId) {
    return ok(views.html.classSelection.render(getUser(), finder.byId(exerciseId)));
  }

  public Result correctClassSelection(int exerciseId) {
    try {
      final ClassSelectionResult result = new ClassSelectionResult(finder.byId(exerciseId), readSolutionFromForm());

      return ok(views.html.classSelectionSolution.render(getUser(), result));
    } catch (final CorrectionException e) {
      Logger.error(ERROR_MSG, e);
      return badRequest(e.getMessage());
    }
  }

  public Result correctDiagramDrawing(int exerciseId) {
    try {
      UmlSolution sol = readSolutionFromForm();
      final DiagramDrawingResult result = new DiagramDrawingResult(finder.byId(exerciseId), sol);

      return ok(views.html.diagdrawingsol.render(getUser(), result));

    } catch (final CorrectionException e) {
      Logger.error(ERROR_MSG, e);
      return badRequest(e.getMessage());
    }
  }

  public Result correctDiagramDrawingWithHelp(int exerciseId) {
    try {
      UmlSolution sol = readSolutionFromForm();
      final DiagramDrawingResult result = new DiagramDrawingResult(finder.byId(exerciseId), sol);

      return ok(views.html.diagdrawinghelpsol.render(getUser(), result));
    } catch (final CorrectionException e) {
      Logger.error(ERROR_MSG, e);
      return badRequest(e.getMessage());
    }
  }

  public Result correctMatching(int exerciseId) {
    return ok(views.html.umlMatchingCorrection.render(getUser()));
  }

  public Result diagramDrawing(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), finder.byId(exerciseId), false));
  }

  public Result diagramDrawingWithHelp(int exerciseId) {
    return ok(views.html.diagdrawing.render(getUser(), finder.byId(exerciseId), true));
  }

  public Result matching(int exerciseId) {
    final UmlExercise exercise = finder.byId(exerciseId);
    return ok(views.html.umlMatching.render(getUser(), exercise, exercise.getSolution()));
  }

  private UmlSolution readSolutionFromForm() throws CorrectionException {
    Optional<UmlSolution> sol = UmlSolution$.MODULE$.readFromForm(factory.form().bindFromRequest());

    if(sol.isPresent()) {
      return sol.get();
    } else {
      throw new CorrectionException("", "");
    }
  }

  @Override
  protected CompleteResult<UmlResult> correct(DynamicForm form, UmlExercise exercise, User user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExercise(User user, UmlExercise exercise) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExesListRest() {
    return EX_LIST_REST;
  }

  @Override
  protected Html renderResult(CompleteResult<UmlResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }

}
