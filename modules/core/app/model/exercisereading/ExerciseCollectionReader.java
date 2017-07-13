package model.exercisereading;

import model.exercise.Exercise;
import model.exercise.ExerciseCollection;

public abstract class ExerciseCollectionReader<E extends Exercise, C extends ExerciseCollection<E>>
    extends ExerciseReader<C> {

  private ExerciseReader<E> delegateReader;

  public ExerciseCollectionReader(ExerciseReader<E> theDelegateReader) {
    super(theDelegateReader.getExerciseType());
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
