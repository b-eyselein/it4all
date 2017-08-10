package controllers.core;

import io.ebean.Finder;
import model.AdminSecured;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;
import model.exercisereading.ExerciseCollectionReader;
import play.data.FormFactory;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public abstract class AExerciseCollectionAdminController<E extends Exercise, C extends ExerciseCollection<E>>
    extends AExerciseAdminController<C> {
  
  public AExerciseCollectionAdminController(FormFactory theFactory, Finder<Integer, C> theFinder,
      ExerciseCollectionReader<E, C> theExerciseReader) {
    super(theFactory, theFinder, theExerciseReader);
  }
  
}
