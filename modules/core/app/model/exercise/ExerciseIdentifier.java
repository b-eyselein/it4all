package model.exercise;

import play.mvc.PathBindable;

public interface ExerciseIdentifier<T extends ExerciseIdentifier<T>> extends PathBindable<T> {
  
}
