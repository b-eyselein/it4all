package controllers.js;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.CommitedTestData;
import model.JsCorrector;
import model.JsExercise;
import model.JsExercise.JsDataType;
import model.JsExerciseIdentifier;
import model.JsExerciseReader;
import model.Util;
import model.result.CompleteResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.correction;
import views.html.error;
import views.html.js;
import views.html.jsoverview;

public class JS extends ExerciseController<model.JsExerciseIdentifier> {
  
  private static String res = "conf/resources/js";
  
  @Inject
  public JS(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
    
    // FIXME: implement and test!
    Path jsonFile = Paths.get(res, "exercises.json");
    Path jsonSchemaFile = Paths.get(res, "exerciseSchema.json");
    (new JsExerciseReader()).readExercises(jsonFile, jsonSchemaFile);
  }
  
  private static List<CommitedTestData> extractAndValidateTestData(DynamicForm form, JsExercise exercise) {
    List<CommitedTestData> testData = new LinkedList<>();
    
    // FIXME: empty testData!
    
    int testCount = Integer.parseInt(form.get("count"));
    int inputCount = Integer.parseInt(form.get("inputs"));
    
    List<JsDataType> dataTypes = exercise.getInputTypes();
    
    for(int testCounter = 0; testCounter < testCount; testCounter++)
      testData.add(readTestDataFromForm(form, dataTypes, inputCount, testCounter, exercise));
    
    JsCorrector.validateTestData(exercise, testData);
    return testData;
  }
  
  private static CommitedTestData readTestDataFromForm(DynamicForm form, List<JsDataType> dataTypes, int inputCount,
      int id, JsExercise exercise) {

    List<String> inputs = new ArrayList<>(inputCount);
    for(int inputCounter = 0; inputCounter < inputCount; inputCounter++) {
      String input = form.get("inp" + inputCounter + ":" + id);
      
      // TODO: Inputtype STRING, NUMBER... ?
      if(dataTypes.get(inputCounter) == JsDataType.STRING)
        input = "\"" + input + "\"";
      
      inputs.add(input);
    }
    
    String output = form.get("outp" + id);
    
    return new CommitedTestData(exercise, id, inputs, output);
    
  }
  
  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    
    CompleteResult testResults = correct(request(), user, new JsExerciseIdentifier(exerciseId));
    
    if(wantsJsonResponse())
      return ok(Json.toJson(testResults));
    else
      return ok(
          // FIXME: Umstellung von Liste auf einzelnes resultat!
          correction.render("Javascript", testResults.getLearnerSolution(), testResults, user));
  }
  
  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();
    JsExercise exercise = JsExercise.finder.byId(id);
    
    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JS.index() + "\">Startseite</a>.</p>")));
    
    return ok(js.render(UserManagement.getCurrentUser(), exercise));
    
  }
  
  public Result index() {
    User user = UserManagement.getCurrentUser();
    return ok(jsoverview.render(user, JsExercise.finder.all()));
  }
  
  public Result validateTestData(int exerciseId) {
    JsExercise exercise = JsExercise.finder.byId(exerciseId);
    if(exercise == null)
      return badRequest();
    
    DynamicForm form = factory.form().bindFromRequest();
    List<CommitedTestData> testData = extractAndValidateTestData(form, exercise);
    
    return ok(Json.toJson(testData));
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, JsExerciseIdentifier identifier) {
    // TODO Auto-generated method stub
    JsExercise exercise = JsExercise.finder.byId(identifier.getId());
    
    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    List<CommitedTestData> userTestData = extractAndValidateTestData(form, exercise);
    // TODO: evtl. Anzeige aussortiertes TestDaten?
    userTestData = userTestData.stream().filter(data -> data.isOk()).collect(Collectors.toList());
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?
    
    return JsCorrector.correct(exercise, learnerSolution, userTestData);
  }
}
