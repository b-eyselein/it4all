package model.exercise;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import model.user.User;
import play.data.validation.Constraints.Required;

@Entity
public class Grading extends Model {
  
  public static Finder<Integer, Grading> finder = new Finder<Integer, Grading>(Grading.class);

  // FIXME: id -> student.id + exercise.id
  @Id
  public int id;

  @ManyToOne
  public User student;

  @ManyToOne
  public Exercise exercise;

  @Required
  public int points;

}
