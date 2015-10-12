package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class Task extends Model {
  
  @Id
  public int id;
  
  @ManyToOne
  public SubExercise exercise;
  
  public String taskDescription;
  public int pts;
  
}
