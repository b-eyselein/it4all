package controllers.programming;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import controllers.core.IdExController;
import model.ApprovalState;
import model.AvailableLanguages;
import model.ProgEvaluationResult;
import model.ProgExercise;
import model.ProgUser;
import model.StringConsts;
import model.exercise.ExerciseOptions;
import model.result.CompleteResult;
import model.testdata.CommitedTestData;
import model.testdata.CommitedTestDataKey;
import model.testdata.ITestData;
import model.user.User;
import play.api.Configuration;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class ProgController extends IdExController<ProgExercise, ProgEvaluationResult> {

  private static final ExerciseOptions EX_OPTIONS = new ExerciseOptions("Programmierung",
      AvailableLanguages.getStdLang().getAceName(), 15, 30, false);

  public static final int STD_TEST_DATA_COUNT = 2;

  @Inject public ProgController(FormFactory f) {
    super(f, ProgExercise.finder, ProgToolObject$.MODULE$);
  }

  @Override public User getUser() {
    final User user = super.getUser();

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

  // private static ProgLangCorrector getCorrector(AvailableLanguages language)
  // {
  // switch(language) {
  // case JAVA_8:
  // return new JavaCorrector();
  // case PYTHON_3:
  // default:
  // return new PythonCorrector();
  // }
  // }

  private static CommitedTestData readTestDataFromForm(DynamicForm form, String username, int testId,
      ProgExercise exercise) {
    final CommitedTestDataKey key = new CommitedTestDataKey(username, exercise.getId(), testId);
    CommitedTestData testdata = CommitedTestData.finder.byId(key);

    if(testdata == null)
      testdata = new CommitedTestData(key);

    testdata.exercise = exercise;
    testdata.inputs = IntStream.range(0, exercise.getInputCount())
        .mapToObj(inputCounter -> form.get("inp_" + inputCounter + "_" + testId))
        .collect(Collectors.joining(ITestData.VALUES_SPLIT_CHAR));

    testdata.output = form.get("outp_" + testId);
    testdata.approvalState = ApprovalState.CREATED;

    return testdata;
  }

  @Override public scala.util.Try<CompleteResult<ProgEvaluationResult>> correctEx(DynamicForm form,
      ProgExercise exercise, User user) {
    // TODO Auto-generated method stub
    return null;
  }

  public Result getDeclaration(String lang) {
    return ok(AvailableLanguages.valueOf(lang).getDeclaration());
  }

  @Override public Html renderExercise(User user, ProgExercise exercise) {
    return views.html.exercise2Rows
        .render(user, toolObject(), EX_OPTIONS, exercise, views.html.progExRest.render(exercise),
            AvailableLanguages.getStdLang().getDeclaration());
  }

  @Override public Html renderExesListRest() {
    // TODO Auto-generated method stub
    return new Html("");
  }

  // private CompleteResult<ProgEvaluationResult> correct(User user,
  // ProgExercise exercise, String learnerSolution,
  // AvailableLanguages lang) throws CorrectionException {
  // // FIXME: Time out der Ausführung
  // try {
  // Logger.debug("Solution: " + learnerSolution);
  //
  // final List<ITestData> completeTestData =
  // Stream.concat(exercise.sampleTestData.stream(),
  // CommitedTestData.forUserAndExercise(user,
  // exercise.getId()).stream()).collect(Collectors.toList());
  //
  // final ProgLangCorrector corrector = getCorrector(lang);
  //
  // final Path solDir = checkAndCreateSolDir(user.name, exercise);
  //
  // return new CompleteResult<>(learnerSolution,
  // corrector.evaluate(learnerSolution, completeTestData, solDir));
  // } catch (final Exception e) {
  // throw new CorrectionException(learnerSolution, "Error while correcting
  // files", e);
  // }
  // }

  @Override public Html renderResult(CompleteResult<ProgEvaluationResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }

  public Result testData(int id) {
    final User user = getUser();
    final List<CommitedTestData> oldTestData = CommitedTestData.forUserAndExercise(user, id);

    return ok(
        views.html.testData.render(user, finder().byId(id), oldTestData != null ? oldTestData : new LinkedList<>()));
  }

  public Result validateTestData(int id) {
    final User user = getUser();
    final ProgExercise exercise = finder().byId(id);
    final DynamicForm form = factory().form().bindFromRequest();
    // final AvailableLanguages language =
    // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME));

    final List<CommitedTestData> testData = extractTestData(form, user.name, exercise);
    testData.forEach(CommitedTestData::save);

    final List<ProgEvaluationResult> validatedTestData = Collections
        .emptyList(); // getCorrector(language).validateTestData(exercise,
    // testData);

    return ok(views.html.validatedTestData.render(user, exercise, validatedTestData));
  }

  public Result validateTestDataLive(int id) {
    // final ProgExercise exercise = finder.byId(id);
    // final DynamicForm form = factory.form().bindFromRequest();
    // final AvailableLanguages language =
    // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME));

    // final List<CommitedTestData> testData = extractTestData(form,
    // getUser().name, exercise);

    final List<ProgEvaluationResult> validatedTestData = Collections
        .emptyList(); // getCorrector(language).validateTestData(exercise,
    // testData);

    return ok(Json.toJson(validatedTestData));
  }
}
