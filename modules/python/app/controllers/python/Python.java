package controllers.python;

import java.util.ArrayList;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
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
import views.html.correction;
import views.html.programming;

public class Python extends ExerciseController<IntExerciseIdentifier> {

  private static final PythonCorrector CORRECTOR = new PythonCorrector();

  @Inject
  public Python(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result commit(IntExerciseIdentifier identifier) {

    User user = UserManagement.getCurrentUser();
    
    CompleteResult result = correct(request(), user, identifier);

    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), identifier, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), identifier, result));
      return ok(correction.render("Javascript", result, user));
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

  public Result index() {
    return ok(programming.render(UserManagement.getCurrentUser(), new PythonExercise()));
  }

  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier identifier) {
    // FIXME: TEST!
    PythonExercise exercise = new PythonExercise(); // PythonExercise.finder.byId(identifier.id);
    
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
