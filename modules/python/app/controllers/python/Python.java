package controllers.python;

import java.util.ArrayList;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.PythonCorrector;
import model.PythonExercise;
import model.Util;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.result.CompleteResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.twirl.api.Html;

public class Python extends ExerciseController {

  private static final PythonCorrector CORRECTOR = new PythonCorrector();

  @Inject
  public Python(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result commit(int id) {

    User user = UserManagement.getCurrentUser();

    CompleteResult result = correct(request(), user, id);

    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), id, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), id, result));
      return ok(views.html.correction.render("Python", new Html("TODO!"), result.getLearnerSolution(), user));
    }

    // DynamicForm form = factory.form().bindFromRequest();
    // String learnerSolution = form.get("editorContent");
    //
    // if(learnerSolution == null || learnerSolution.isEmpty())
    // return ok(Json.toJson(new EvaluationFailed("Sie haben einen leeren String
    // abgegeben!")));
    //
    // IExecutionResult result = CORRECTOR.execute(learnerSolution);
    //
    // return ok(Json.toJson(new
    // GenericEvaluationResult(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE,
    // "The result was: " + result.getResult(), "The output was:\n" +
    // result.getOutput())));
  }

  public Result exercise(int id) {
    return ok(views.html.programming.render(UserManagement.getCurrentUser(), PythonExercise.finder.byId(id)));
  }

  public Result index() {
    return ok(views.html.pythonoverview.render(UserManagement.getCurrentUser(), PythonExercise.finder.all()));
  }

  protected CompleteResult correct(Request request, User user, int id) {
    // FIXME: TEST!
    PythonExercise exercise = new PythonExercise(id); // PythonExercise.finder.byId(identifier.id);

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

}
