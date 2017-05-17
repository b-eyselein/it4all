package controllers.programming;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.ApprovalState;
import model.ProgEvaluationResult;
import model.ProgExercise;
import model.ProgLanguage;
import model.ProgrammingUser;
import model.StringConsts;
import model.Util;
import model.correctors.JsCorrector;
import model.correctors.ProgLangCorrector;
import model.correctors.PythonCorrector;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.testdata.CommitedTestData;
import model.testdata.CommitedTestDataKey;
import model.testdata.ITestData;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;

public class Prog extends ExerciseController {
  
  public static final int STD_TEST_DATA_COUNT = 2;
  
  private static final ProgLangCorrector JS_CORRECTOR = new JsCorrector();
  private static final ProgLangCorrector PYTHON_CORRECTOR = new PythonCorrector();
  
  @Inject
  public Prog(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public static User getUser() {
    User user = ExerciseController.getUser();
    
    if(ProgrammingUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new ProgrammingUser(user.name).save();
    
    return user;
  }
  
  private static List<CommitedTestData> extractTestData(DynamicForm form, String username, ProgExercise exercise) {
    return IntStream.range(0, Integer.parseInt(form.get("testCount")))
        .mapToObj(testCounter -> readTestDataFromForm(form, username, testCounter, exercise))
        .collect(Collectors.toList());
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
    
    if(testdata == null)
      testdata = new CommitedTestData(key);
    
    testdata.exercise = exercise;
    testdata.inputs = IntStream.range(0, exercise.inputCount)
        .mapToObj(inputCounter -> form.get("inp_" + inputCounter + "_" + testId))
        .collect(Collectors.joining(ITestData.VALUES_SPLIT_CHAR));
    
    testdata.output = form.get("outp_" + testId);
    testdata.approvalState = ApprovalState.CREATED;
    
    return testdata;
  }
  
  public Result correct(int id, String language) {
    User user = getUser();
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    
    List<ProgEvaluationResult> result = correct(user.name, ProgExercise.finder.byId(id), learnerSolution,
        ProgLanguage.valueOf(language));
    
    log(user, new ExerciseCompletionEvent(request(), id, result));
    return ok(views.html.correction.render("Programmiersprachen", new play.twirl.api.Html(result.toString()),
        learnerSolution, user));
  }
  
  public Result correctLive(int id, String language) {
    User user = getUser();
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    
    List<ProgEvaluationResult> result = correct(user.name, ProgExercise.finder.byId(id), learnerSolution,
        ProgLanguage.valueOf(language));
    
    log(user, new ExerciseCorrectionEvent(request(), id, result));
    return ok(views.html.progResult.render(result));
    
  }
  
  public Result exercise(int id, String language) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);
    
    if(exercise == null)
      return redirect(routes.Prog.index());
    
    Request request = request();
    log(user, new ExerciseStartEvent(request, id));
    return ok(views.html.progExercise.render(getUser(), exercise, ProgLanguage.valueOf(language)));
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
    
    return ok(views.html.testData.render(user, ProgExercise.finder.byId(id),
        oldTestData != null ? oldTestData : new LinkedList<>()));
  }
  
  public Result validateTestData(int id) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    
    List<CommitedTestData> testData = extractTestData(form, user.name, exercise);
    testData.forEach(CommitedTestData::save);
    
    List<ProgEvaluationResult> validatedTestData = PYTHON_CORRECTOR.validateTestData(exercise, testData);
    
    return ok(views.html.validatedTestData.render(user, exercise, validatedTestData));
  }
  
  public Result validateTestDataLive(int id) {
    ProgExercise exercise = ProgExercise.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    
    List<CommitedTestData> testData = extractTestData(form, getUser().name, exercise);
    
    List<ProgEvaluationResult> validatedTestData = PYTHON_CORRECTOR.validateTestData(exercise, testData);
    
    return ok(Json.toJson(validatedTestData));
  }
  
  private List<ProgEvaluationResult> correct(String username, ProgExercise exercise, String learnerSolution,
      ProgLanguage lang) {
    // FIXME: Time out der Ausführung
    
    List<CommitedTestData> userTestData = CommitedTestData.forUserAndExercise(username, exercise.id);
    
    ProgLangCorrector corrector = getProgLangCorrector(lang);
    
    return corrector.correct(exercise, learnerSolution, userTestData);
  }
}
