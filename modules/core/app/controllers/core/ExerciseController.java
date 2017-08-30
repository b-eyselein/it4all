package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.ebean.Finder;
import model.CorrectionException;
import model.Secured;
import model.exercise.Exercise;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(Secured.class)
public abstract class ExerciseController<E extends Exercise, R extends EvaluationResult> extends BaseController {

  protected final Finder<Integer, E> finder;
  protected final String exerciseType;

  public ExerciseController(FormFactory theFactory, String theExerciseType, Finder<Integer, E> theFinder) {
    super(theFactory);
    finder = theFinder;
    exerciseType = theExerciseType;
  }

  public Result correct(int id) {
    try {
      User user = getUser();
      CompleteResult<R> correctionResult = correct(factory.form().bindFromRequest(), finder.byId(id), user);

      log(user, new ExerciseCompletionEvent(request(), id, correctionResult));

      return ok(renderCorrectionResult(user, correctionResult));
    } catch (CorrectionException e) {
      return badRequest("TODO!");
    }
  }

  public Result correctLive(int id) {
    try {
      User user = getUser();
      CompleteResult<R> correctionResult = correct(factory.form().bindFromRequest(), finder.byId(id), user);

      log(user, new ExerciseCorrectionEvent(request(), id, correctionResult));

      return ok(renderResult(correctionResult));
    } catch (CorrectionException e) {
      return badRequest(Json.toJson(e.getMessage()));
    }
  }

  public Result exercise(int id) {
    User user = getUser();
    E exercise = finder.byId(id);

    if(exercise == null)
      return redirect(controllers.routes.Application.index());

    log(user, new ExerciseStartEvent(request(), id));

    return ok(renderExercise(user, exercise));
  }

  protected Path checkAndCreateSolDir(String username, Exercise exercise) {
    Path dir = getSolDirForExercise(username, exerciseType, exercise);

    if(dir.toFile().exists())
      return dir;

    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      Logger.error("There was an error while creating the directory for an " + exerciseType + " solution: " + dir, e);
      return null;
    }
  }

  protected abstract CompleteResult<R> correct(DynamicForm form, E exercise, User user) throws CorrectionException;

  protected Path getSampleDir() {
    return getSampleDir(exerciseType);
  }

  protected Html renderCorrectionResult(User user, CompleteResult<R> correctionResult) {
    return views.html.correction.render(exerciseType.toUpperCase(), renderResult(correctionResult),
        correctionResult.getLearnerSolution(), user, controllers.routes.Application.index());
  }

  protected abstract Html renderExercise(User user, E exercise);

  protected abstract Html renderResult(CompleteResult<R> correctionResult);

}
