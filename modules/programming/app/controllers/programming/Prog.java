package controllers.programming;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.ApprovalState;
import model.AvailableLanguages;
import model.ProgCorrectionException;
import model.ProgEvaluationResult;
import model.ProgExercise;
import model.ProgUser;
import model.StringConsts;
import model.correction.CorrectionException;
import model.correctors.JavaCorrector;
import model.correctors.ProgLangCorrector;
import model.correctors.PythonCorrector;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.testdata.CommitedTestData;
import model.testdata.CommitedTestDataKey;
import model.testdata.ITestData;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;

public class Prog extends ExerciseController {

  public static final int STD_TEST_DATA_COUNT = 2;

  @Inject
  public Prog(FormFactory theFactory) {
    super(theFactory, "prog");
  }

  public static User getUser() {
    User user = ExerciseController.getUser();

    if(ProgUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new ProgUser(user.name).save();

    return user;
  }

  private static List<CommitedTestData> extractTestData(DynamicForm form, String username, ProgExercise exercise) {
    return IntStream.range(0, Integer.parseInt(form.get(StringConsts.TEST_COUNT_NAME)))
        .mapToObj(testCounter -> readTestDataFromForm(form, username, testCounter, exercise))
        .collect(Collectors.toList());
  }

  private static ProgLangCorrector getCorrector(AvailableLanguages language) {
    switch(language) {
    case JAVA_8:
      return new JavaCorrector();
    case PYTHON_3:
    default:
      return new PythonCorrector();
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

  public Result correct(int id) {
    User user = getUser();

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    String language = form.get(StringConsts.LANGUAGE_NAME);

    try {
      List<ProgEvaluationResult> result = correct(user, ProgExercise.finder.byId(id), learnerSolution,
          AvailableLanguages.valueOf(language));

      log(user, new ExerciseCompletionEvent(request(), id, result));
      return ok(views.html.correction.render("Programmiersprachen", views.html.progResult.render(result),
          learnerSolution, user));
    } catch (CorrectionException e) {
      Logger.error("Error while correcting programming", e);
      return badRequest();
    }
  }

  public Result correctLive(int id) {
    User user = getUser();

    DynamicForm form = factory.form().bindFromRequest();

    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    String language = form.get(StringConsts.LANGUAGE_NAME);

    try {
      List<ProgEvaluationResult> result = correct(user, ProgExercise.finder.byId(id), learnerSolution,
          AvailableLanguages.valueOf(language));

      log(user, new ExerciseCorrectionEvent(request(), id, result));
      return ok(views.html.progResult.render(result));
    } catch (CorrectionException e) {
      Logger.error("Error while correcting programming live", e);
      return badRequest();
    }
  }

  public Result exercise(int id) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);

    if(exercise == null)
      return redirect(routes.Prog.index());

    Request request = request();
    log(user, new ExerciseStartEvent(request, id));
    return ok(views.html.progExercise.render(getUser(), exercise));
  }

  public Result exercises() {
    return ok(views.html.exercises.render(getUser(), ProgExercise.finder.all()));
  }

  public Result getDeclaration(String lang) {
    return ok(AvailableLanguages.valueOf(lang).getDeclaration());
  }

  public Result index() {
    return ok(views.html.progIndex.render(getUser(), ProgExercise.finder.all()));
  }

  public Result testData(int id) {
    User user = getUser();
    List<CommitedTestData> oldTestData = CommitedTestData.forUserAndExercise(user, id);

    return ok(views.html.testData.render(user, ProgExercise.finder.byId(id),
        oldTestData != null ? oldTestData : new LinkedList<>()));
  }

  public Result validateTestData(int id) {
    User user = getUser();
    ProgExercise exercise = ProgExercise.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    AvailableLanguages language = AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME));

    List<CommitedTestData> testData = extractTestData(form, user.name, exercise);
    testData.forEach(CommitedTestData::save);

    List<ProgEvaluationResult> validatedTestData = Collections.emptyList(); // getCorrector(language).validateTestData(exercise,
                                                                            // testData);

    return ok(views.html.validatedTestData.render(user, exercise, validatedTestData));
  }

  public Result validateTestDataLive(int id) {
    ProgExercise exercise = ProgExercise.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    AvailableLanguages language = AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME));

    List<CommitedTestData> testData = extractTestData(form, getUser().name, exercise);

    List<ProgEvaluationResult> validatedTestData = Collections.emptyList(); // getCorrector(language).validateTestData(exercise,
                                                                            // testData);

    return ok(Json.toJson(validatedTestData));
  }

  private List<ProgEvaluationResult> correct(User user, ProgExercise exercise, String learnerSolution,
      AvailableLanguages lang) throws CorrectionException {
    // FIXME: Time out der Ausführung
    try {
      Logger.debug("Solution: " + learnerSolution);

      List<ITestData> completeTestData = Stream
          .concat(exercise.sampleTestData.stream(), CommitedTestData.forUserAndExercise(user, exercise.id).stream())
          .collect(Collectors.toList());

      ProgLangCorrector corrector = getCorrector(lang);

      Path solDir = checkAndCreateSolDir(exercise);

      return corrector.evaluate(learnerSolution, completeTestData, solDir);
    } catch (Exception e) {
      throw new ProgCorrectionException(learnerSolution, "Error while correcting files", e);
    }
  }
}
