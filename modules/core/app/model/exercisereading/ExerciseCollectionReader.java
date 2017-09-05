package model.exercisereading;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;

public abstract class ExerciseCollectionReader<E extends Exercise, C extends ExerciseCollection<E>>
    extends JsonReader<C> {

  protected ExerciseReader<E> delegateReader;

  public ExerciseCollectionReader(String theExerciseType, ExerciseReader<E> theDelegateReader,
      Finder<Integer, C> theFinder, Class<?> theClassFor) {
    super(theExerciseType, theFinder, theClassFor);
    delegateReader = theDelegateReader;
  }

  public ExerciseReader<E> getDelegateReader() {
    return delegateReader;
  }
  
  public void saveExercise(C exercise) {
    exercise.save();
    exercise.getExercises().forEach(Exercise::save);
  }
}
