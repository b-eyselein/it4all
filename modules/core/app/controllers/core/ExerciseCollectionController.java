package controllers.core;

import io.ebean.Finder;
import model.Secured;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(Secured.class)
public abstract class ExerciseCollectionController<E extends Exercise, C extends ExerciseCollection<E>>
    extends BaseController {

  protected final Finder<Integer, C> finder;
  protected final String exerciseType;

  public ExerciseCollectionController(FormFactory theFactory, String theExerciseType, Finder<Integer, C> theFinder) {
    super(theFactory);
    finder = theFinder;
    exerciseType = theExerciseType;
  }

  public Result exercises(int id) {
    C exCollection = finder.byId(id);

    for(E exercise: exCollection)
      System.out.println(exercise);

    return ok("TODO!");
  }

}
