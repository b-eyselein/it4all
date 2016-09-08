package controllers.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.exercise.EvaluationResult;
import model.javascript.JsCorrector;
import model.javascript.JsExercise;
import model.javascript.JsExercise.JsDataType;
import model.javascript.CommitedTestData;
import model.javascript.web.JsWebExercise;
import model.javascript.web.JsWebTestResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.error;
import views.html.javascript.js;
import views.html.javascript.jscorrect;
import views.html.javascript.jsoverview;
import views.html.javascript.jsweb;

@Security.Authenticated(Secured.class)
public class JS extends Controller {
  
  private static final String EXERCISE_TYPE = "js";
  private static final String FILE_TYPE = "html";
  
  private static List<CommitedTestData> extractAndValidateTestData(DynamicForm form, JsExercise exercise) {
    List<CommitedTestData> testData = new LinkedList<>();
    
    int testCount = Integer.parseInt(form.get("count"));
    int inputCount = Integer.parseInt(form.get("inputs"));
    
    List<JsDataType> dataTypes = exercise.getInputTypes();
    
    for(int testCounter = 0; testCounter < testCount; testCounter++) {
      List<String> theInput = readTestDataFromForm(form, dataTypes, inputCount, testCounter);
      if(theInput != null)
        testData.add(new CommitedTestData(exercise, testCounter, theInput));
    }
    
    JsCorrector.validateTestData(exercise, testData);
    return testData;
  }
  
  private static List<String> readTestDataFromForm(DynamicForm form, List<JsDataType> dataTypes, int inputCount,
      int testCounter) {
    List<String> testData = new ArrayList<>(inputCount + 1);
    for(int inputCounter = 0; inputCounter < inputCount; inputCounter++) {
      String input = form.get("inp" + inputCounter + ":" + testCounter);
      
      if(input == null || input.isEmpty())
        return null;
      
      // TODO: Inputtype STRING, NUMBER... ?
      if(dataTypes.get(inputCounter) == JsDataType.STRING)
        input = "\"" + input + "\"";
      
      testData.add(input);
    }
    
    String output = form.get("outp" + testCounter);
    if(output == null || output.isEmpty())
      return null;
    testData.add(output);
    
    return testData;
  }
  
  @Inject
  private Util util;
  
  @Inject
  private FormFactory factory;
  
  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    
    JsExercise exercise = JsExercise.finder.byId(exerciseId);
    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existiert nicht!</p><p>Zur&uuml;ck zur Startseite</p>"));
    
    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    
    String learnerSolution = form.get("editorContent");
    List<CommitedTestData> userTestData = extractAndValidateTestData(form, exercise);
    // TODO: evtl. Anzeige aussortiertes TestDaten?
    userTestData = userTestData.stream().filter(data -> data.isOk()).collect(Collectors.toList());
    
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?
    
    List<EvaluationResult> testResults = JsCorrector.correct(exercise, learnerSolution, userTestData);
    
    // Logger.debug("Return:\n" + Json.prettyPrint(Json.toJson(testResults)));
    
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(testResults));
    else
      return ok(jscorrect.render(learnerSolution, testResults, user));
  }
  
  public Result commitWeb(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    JsWebExercise exercise = JsWebExercise.finder.byId(exerciseId);
    
    try {
      saveSolutionForUser(user, learnerSolution, exerciseId);
    } catch (IOException e) {
      Logger.error("Error while saving file ", e);
      return badRequest("Error while saving file!");
    }
    
    String solutionUrl = "http://localhost:9000" + routes.Solution.site(user, "js", exercise.id).url();
    
    List<JsWebTestResult> result = JsCorrector.correctWeb(exercise, solutionUrl);
    
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(result));
    else
      // FIXME: Implementierung Übersicht Endkorrektur Javascript Web!
      return ok("Diese Seite muss noch erstellt werden!");
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
  
  public Result exerciseWeb(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    JsWebExercise exercise = JsWebExercise.finder.byId(exerciseId);
    
    if(exercise == null)
      return badRequest(
          error.render(user, new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
              + routes.JS.index() + "\">Startseite</a>.</p>")));
    
    String oldSolution = exercise.declaration;
    try {
      Path file = util.getSolFileForExercise(user, "js", exerciseId, FILE_TYPE);
      if(Files.exists(file))
        oldSolution = String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      Logger.error("Error while loading old JsWeb solution: ", e);
    }
    
    return ok(jsweb.render(user, exercise, oldSolution));
  }
  
  public Result index() {
    User user = UserManagement.getCurrentUser();
    return ok(jsoverview.render(user, JsExercise.finder.all(), JsWebExercise.finder.all()));
  }
  
  public Result validateTestData(int exerciseId) {
    JsExercise exercise = JsExercise.finder.byId(exerciseId);
    if(exercise == null)
      return badRequest();
    
    DynamicForm form = factory.form().bindFromRequest();
    List<CommitedTestData> testData = extractAndValidateTestData(form, exercise);
    
    return ok(Json.toJson(testData));
  }
  
  private void saveSolutionForUser(User user, String solution, int exercise) throws IOException {
    Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
    if(!Files.exists(solDir))
      Files.createDirectories(solDir);
    
    Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise, FILE_TYPE);
    Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
  
}
