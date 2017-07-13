package model.exercise;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Model;
import model.WithId;

@MappedSuperclass
public abstract class ExerciseCollection<E extends Exercise> extends Model implements WithId {
  
  @Id
  protected int id;
  
  protected String title;
  
  public ExerciseCollection(int theId, String theTitle) {
    id = theId;
    title = theTitle;
  }
  
  public abstract List<E> getExercises();
  
  @Override
  public int getId() {
    return id;
  }
  
  @JsonIgnore
  public int getMaxPoints() {
    return 0;
    // return exercises.parallelStream().mapToInt(q -> q.maxPoints).sum();
  }
  
  public String getTitle() {
    return title;
  }
}
