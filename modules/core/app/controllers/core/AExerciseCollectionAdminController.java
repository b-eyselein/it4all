package controllers.core;

import io.ebean.Finder;
import model.AdminSecured;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;
import model.exercisereading.ExerciseCollectionReader;
import play.data.FormFactory;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public class AExerciseCollectionAdminController<E extends Exercise, C extends ExerciseCollection<E>>
    extends BaseController {

  protected Finder<Integer, C> finder;
  protected ExerciseCollectionReader<E, C> exerciseReader;
  
  public AExerciseCollectionAdminController(FormFactory theFactory, Finder<Integer, C> theFinder,
      ExerciseCollectionReader<E, C> theExerciseReader) {
    super(theFactory);
    finder = theFinder;
    exerciseReader = theExerciseReader;
  }
  
}
