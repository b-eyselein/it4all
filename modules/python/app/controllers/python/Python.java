package controllers.python;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.PythonCorrector;
import model.Util;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.programming.IExecutionResult;
import model.result.CompleteResult;
import model.result.EvaluationFailed;
import model.result.GenericEvaluationResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.Request;
import play.mvc.Result;
import views.html.python;

public class Python extends ExerciseController<IntExerciseIdentifier> {
  
  private static final PythonCorrector CORRECTOR = new PythonCorrector();
  
  @Inject
  public Python(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result commit() {
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");

    if(learnerSolution == null || learnerSolution.isEmpty())
      return ok(Json.toJson(new EvaluationFailed("Sie haben einen leeren String abgegeben!")));

    IExecutionResult result = CORRECTOR.execute(learnerSolution);
    
    return ok(Json.toJson(new GenericEvaluationResult(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE,
        "The result was: " + result.getResult(), "The output was:\n" + result.getOutput())));
  }
  
  public Result index() {
    return ok(python.render(UserManagement.getCurrentUser()));
  }
  
  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier exercise) {
    // FIXME: implement!
    return null;
  }
  
}
