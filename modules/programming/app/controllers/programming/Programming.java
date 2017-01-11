package controllers.programming;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.CommitedTestData;
import model.ProgrammingExercise;
import model.Util;
import model.js.JsCorrector;
import model.js.JsDataType;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import views.html.correction;
import views.html.programming;
import views.html.progoverview;

public class Programming extends ExerciseController {
  
  private static final JsCorrector CORRECTOR = new JsCorrector();
  
  @Inject
  public Programming(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  // private static List<CommitedTestData>
  // extractAndValidateTestData(DynamicForm form, JsExercise exercise) {
  // // FIXME: empty testData!
  //
  // int testCount = Integer.parseInt(form.get("count"));
  // int inputCount = Integer.parseInt(form.get("inputs"));
  //
  // // List<JsDataType> dataTypes = exercise.getInputTypes();
  //
  // List<CommitedTestData> testData = new LinkedList<>();
  //
  // for(int testCounter = 0; testCounter < testCount; testCounter++)
  // testData.add(readTestDataFromForm(form, dataTypes, inputCount, testCounter,
  // exercise));
  //
  // CORRECTOR.validateTestData(exercise, testData);
  // return testData;
  // }
  
  private static CommitedTestData readTestDataFromForm(DynamicForm form, List<JsDataType> dataTypes, int inputCount,
      int id, ProgrammingExercise exercise) {
    
    List<String> inputs = new ArrayList<>(inputCount);
    for(int inputCounter = 0; inputCounter < inputCount; inputCounter++) {
      String input = form.get("inp" + inputCounter + ":" + id);
      
      // TODO: Inputtype STRING, NUMBER... ?
      // if(dataTypes.get(inputCounter) == JsDataType.STRING)
      // input = "\"" + input + "\"";
      
      inputs.add(input);
    }
    
    String output = form.get("outp" + id);
    
    return new CommitedTestData(id, inputs, output);
  }
  
  public Result commit(int id) {
    User user = UserManagement.getCurrentUser();
    
    CompleteResult result = correct(request(), user, id);
    
    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), id, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), id, result));
      return ok(correction.render("Javascript", result, user));
    }
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, int id) {
    // FIXME: TEST!
    ProgrammingExercise exercise = ProgrammingExercise.finder.byId(id);
    
    // FIXME: Time out der Ausführung
    
    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    // List<CommitedTestData> userTestData = extractAndValidateTestData(form,
    // exercise);
    // TODO: evtl. Anzeige aussortiertes TestDaten?
    // userTestData =
    // userTestData.stream().filter(CommitedTestData::isOk).collect(Collectors.toList());
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?
    
    return CORRECTOR.correct(exercise, learnerSolution, new ArrayList<>(/* userTestData */), user.todo);
  }
  
  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();
    ProgrammingExercise exercise = ProgrammingExercise.finder.byId(id);
    
    // if(exercise == null)
    // return redirect(controllers.programming.routes.Programming.index());
    
    log(user, new ExerciseStartEvent(request(), id));
    return ok(programming.render(UserManagement.getCurrentUser(), exercise));
  }
  
  public Result index() {
    return ok(progoverview.render(UserManagement.getCurrentUser(), ProgrammingExercise.finder.all()));
  }
  
  public Result validateTestData(int id) {
    ProgrammingExercise exercise = ProgrammingExercise.finder.byId(id);
    
    // List<CommitedTestData> testData =
    // extractAndValidateTestData(factory.form().bindFromRequest(), exercise);
    
    return ok(Json.toJson(/* testData */null));
  }
}
