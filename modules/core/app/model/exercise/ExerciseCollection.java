package model.exercise;

import java.util.List;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import model.WithId;

@MappedSuperclass
public abstract class ExerciseCollection<E extends Exercise> extends Exercise implements WithId {

  public ExerciseCollection(int theId, String theTitle, String theAuthor, String theText) {
    super(theId, theTitle, theAuthor, theText);
  }

  public abstract List<E> getExercises();

  @JsonIgnore
  public int getMaxPoints() {
    return 0;
    // return exercises.parallelStream().mapToInt(q -> q.maxPoints).sum();
  }

}
