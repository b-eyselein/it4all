package model.exercisereading;

import io.ebean.Finder;
import model.exercise.Exercise;
import model.exercise.ExerciseCollection;

public abstract class ExerciseCollectionReader<E extends Exercise, C extends ExerciseCollection<E>>
    extends ExerciseReader<C> {

  protected ExerciseReader<E> delegateReader;

  public ExerciseCollectionReader(ExerciseReader<E> theDelegateReader, Finder<Integer, C> theFinder,
      Class<?> theClassFor) {
    super(theDelegateReader.getExerciseType(), theFinder, theClassFor);
    delegateReader = theDelegateReader;
  }

  public ExerciseReader<E> getDelegateReader() {
    return delegateReader;
  }

  @Override
  public void saveRead(C exercise) {
    exercise.save();
    exercise.getExercises().forEach(Exercise::save);
  }

}
