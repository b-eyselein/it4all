package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.ebean.Finder;
import model.Secured;
import model.StringConsts;
import model.exercise.Exercise;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(Secured.class)
public abstract class ExerciseController<E extends Exercise, R extends EvaluationResult>
    extends BaseExerciseController {

  private final Finder<Integer, E> finder;
  
  public ExerciseController(FormFactory theFactory, String theExerciseType, Finder<Integer, E> theFinder) {
    super(theFactory, theExerciseType);
    finder = theFinder;
  }
  
  public Result correct(int id) {
    User user = getUser();
    E exercise = finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    
    List<R> correctionResult = correct(learnerSolution, exercise, user);
    
    log(user, new ExerciseCompletionEvent(request(), id, correctionResult));
    
    return ok(views.html.correction.render(exerciseType.toUpperCase(), renderResult(correctionResult), learnerSolution,
        user, controllers.routes.Application.index()));
  }

  public Result correctLive(int id) {
    User user = getUser();
    E exercise = finder.byId(id);

    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);

    List<R> correctionResult = correct(learnerSolution, exercise, user);

    log(user, new ExerciseCorrectionEvent(request(), id, correctionResult));

    return ok(renderResult(correctionResult));
  }

  protected Path checkAndCreateSolDir(Exercise exercise) {
    Path dir = getSolDirForExercise(exercise);

    if(dir.toFile().exists())
      return dir;

    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      Logger.error("There was an error while creating the directory for an " + exerciseType + " solution: " + dir, e);
      return null;
    }
  }
  
  protected abstract List<R> correct(String learnerSolution, E exercise, User user);
  
  protected abstract Html renderResult(List<R> correctionResult);
  
}
