package controllers.programming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.ApprovalState;
import model.ProgEvaluationResult;
import model.ProgExercise;
import model.ProgLanguage;
import model.ProgrammingUser;
import model.Util;
import model.correctors.JsCorrector;
import model.correctors.ProgLangCorrector;
import model.correctors.PythonCorrector;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.testdata.CommitedTestData;
import model.testdata.CommitedTestDataKey;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;

public class Programming extends ExerciseController {
  
  private static final int TEST_COUNT = 4;
  
  private static final ProgLangCorrector JS_CORRECTOR = new JsCorrector();
  private static final ProgLangCorrector PYTHON_CORRECTOR = new PythonCorrector();
  
  public static User getUser() {
    User user = ExerciseController.getUser();
    
    if(ProgrammingUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new ProgrammingUser(user.name).save();
    
    return user;
  }
  
  private static List<CommitedTestData> extractTestData(DynamicForm form, String username, ProgExercise exercise) {
    List<CommitedTestData> testData = new ArrayList<>(TEST_COUNT);
    
    for(int testCounter = 0; testCounter < TEST_COUNT; testCounter++)
      testData.add(readTestDataFromForm(form, username, testCounter, exercise));
    
    return testData;
  }
  
  private static ProgLangCorrector getProgLangCorrector(ProgLanguage lang) {
    switch(lang) {
    case JS:
      return JS_CORRECTOR;
    case PYTHON:
    default:
      return PYTHON_CORRECTOR;
    }
  }
  
  private static CommitedTestData readTestDataFromForm(DynamicForm form, String username, int testId,
      ProgExercise exercise) {
    CommitedTestDataKey key = new CommitedTestDataKey(username, exercise.id, testId);
    CommitedTestData testdata = CommitedTestData.finder.byId(key);
    
    List<String> inputs = new ArrayList<>(exercise.inputCount);
    for(int inputCounter = 0; inputCounter < exercise.inputCount; inputCounter++)
      inputs.add(form.get("inp_" + inputCounter + "_" + testId));
    
    if(testdata == null)
      testdata = new CommitedTestData(key);
    
    testdata.exercise = exercise;
    testdata.inputs = String.join("#", inputs);
    testdata.output = form.get("outp_" + testId);
    testdata.approvalState = ApprovalState.CREATED;
    
    testdata.save();
    
    return testdata;
  }
  
  @Inject
  public Programming(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result commit(int id) {
    User user = getUser();
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    String language = form.get("language");
    
    CompleteResult result = correct(ProgExercise.finder.byId(id), learnerSolution, ProgLanguage.valueOf(language));
    
    Request request = request();
    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request, id, result.getResults()));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request, id, result.getResults()));
      return ok(views.html.correction.render("Programmiersprachen", new play.twirl.api.Html(result.toString()),
          learnerSolution, user));
    }
  }
  
  public Result exercise(int id) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);
    
    if(exercise == null)
      return redirect(controllers.programming.routes.Programming.index().url());
    
    Request request = request();
    log(user, new ExerciseStartEvent(request, id));
    return ok(views.html.progExercise.render(getUser(), exercise));
  }
  
  public Result exercises() {
    return ok(views.html.exercises.render(getUser(), ProgExercise.finder.all()));
  }
  
  public Result index() {
    return ok(views.html.progIndex.render(getUser(), ProgExercise.finder.all()));
  }
  
  public Result testData(int id) {
    User user = getUser();
    List<CommitedTestData> oldTestData = CommitedTestData.finder.where().eq("user_name", user.name).and()
        .eq("exercise_id", id).findList();

    if(oldTestData == null || oldTestData.isEmpty())
      oldTestData = Arrays.asList(new CommitedTestData(new CommitedTestDataKey(user.name, id, 0), "1#2", "3"),
          new CommitedTestData(new CommitedTestDataKey(user.name, id, 1), "4#5", "6"),
          new CommitedTestData(new CommitedTestDataKey(user.name, id, 2), "7#8", "15"));

    return ok(views.html.testData.render(user, ProgExercise.finder.byId(id), oldTestData));
  }
  
  public Result validateTestData(int id) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    
    List<CommitedTestData> testData = extractTestData(form, user.name, exercise);
    
    testData.forEach(System.out::println);
    
    List<ProgEvaluationResult> validatedTestData = PYTHON_CORRECTOR.validateTestData(exercise, testData);
    
    validatedTestData.forEach(System.out::println);
    
    return ok(Json.toJson(validatedTestData));
  }
  
  private CompleteResult correct(ProgExercise exercise, String learnerSolution, ProgLanguage lang) {
    // // FIXME: TEST!
    
    // FIXME: Time out der Ausführung
    
    // List<CommitedTestData> userTestData = extractTestData(form, exercise);
    // TODO: evtl. Anzeige aussortiertes TestDaten?
    // userTestData =
    
    // userTestData.stream().filter(CommitedTestData::isOk).collect(Collectors.toList());
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?
    
    ProgLangCorrector corrector = getProgLangCorrector(lang);
    
    return corrector.correct(exercise, learnerSolution, new ArrayList<>(/* userTestData */));
  }
}
