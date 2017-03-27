package controllers.js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.JsCorrector;
import model.JsExercise;
import model.JsExercise.JsDataType;
import model.Util;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.programming.CommitedTestData;
import model.programming.ExecutionResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class JS extends ExerciseController {

  private static final JsCorrector CORRECTOR = new JsCorrector();

  @Inject
  public JS(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  private static List<CommitedTestData> extractAndValidateTestData(DynamicForm form, JsExercise exercise) {
    // FIXME: empty testData!

    int testCount = Integer.parseInt(form.get("count"));
    int inputCount = Integer.parseInt(form.get("inputs"));

    List<JsDataType> dataTypes = exercise.getInputTypes();

    List<CommitedTestData> testData = new LinkedList<>();

    for(int testCounter = 0; testCounter < testCount; testCounter++)
      testData.add(readTestDataFromForm(form, dataTypes, inputCount, testCounter));

    CORRECTOR.validateTestData(exercise, testData);
    return testData;
  }

  private static CommitedTestData readTestDataFromForm(DynamicForm form, List<JsDataType> dataTypes, int inputCount,
      int id) {

    List<String> inputs = new ArrayList<>(inputCount);
    for(int inputCounter = 0; inputCounter < inputCount; inputCounter++) {
      String input = form.get("inp" + inputCounter + ":" + id);

      // TODO: Inputtype STRING, NUMBER... ?
      if(dataTypes.get(inputCounter) == JsDataType.STRING)
        input = "\"" + input + "\"";

      inputs.add(input);
    }

    String output = form.get("outp" + id);

    return new CommitedTestData(id, inputs, output);
  }

  public Result correct(int id) {
    User user = UserManagement.getCurrentUser();
    JsExercise exercise = JsExercise.finder.byId(id);

    // FIXME: Time out der Ausführung

    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<CommitedTestData> userTestData = extractAndValidateTestData(form, exercise);

    List<ExecutionResult> result = correct(learnerSolution, exercise, userTestData);

    log(user, new ExerciseCompletionEvent(request(), id, result));
    return ok(views.html.correction.render("Javascript", views.html.progresult.render(result), learnerSolution, user));
  }

  public Result correctLive(int id) {
    User user = UserManagement.getCurrentUser();
    JsExercise exercise = JsExercise.finder.byId(id);

    // FIXME: Time out der Ausführung

    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<CommitedTestData> userTestData = extractAndValidateTestData(form, exercise);

    List<ExecutionResult> result = correct(learnerSolution, exercise, userTestData);

    log(user, new ExerciseCorrectionEvent(request(), id, result));
    return ok(views.html.progresult.render(result));
  }

  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();
    JsExercise exercise = JsExercise.finder.byId(id);

    if(exercise == null)
      return badRequest(
          views.html.error.render(user, "<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JS.index() + "\">Startseite</a>.</p>"));

    log(user, new ExerciseStartEvent(request(), id));
    return ok(views.html.programming.render(UserManagement.getCurrentUser(), exercise));
  }

  public Result index() {
    return ok(views.html.jsoverview.render(UserManagement.getCurrentUser(), JsExercise.finder.all()));
  }

  public Result validateTestData(int id) {
    JsExercise exercise = JsExercise.finder.byId(id);
    if(exercise == null)
      return badRequest();

    List<CommitedTestData> testData = extractAndValidateTestData(factory.form().bindFromRequest(), exercise);

    return ok(Json.toJson(testData));
  }

  protected List<ExecutionResult> correct(String learnerSolution, JsExercise exercise,
      List<CommitedTestData> userTestData) {
    // FIXME: TEST!

    // TODO: evtl. Anzeige aussortiertes TestDaten?
    userTestData = userTestData.stream().filter(CommitedTestData::isOk).collect(Collectors.toList());
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?

    return CORRECTOR.correct(exercise, learnerSolution, new ArrayList<>(userTestData));
  }
}
