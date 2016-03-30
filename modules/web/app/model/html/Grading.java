package model.html;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import model.user.Student;
import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class Grading extends Model {
  
  public static Finder<Integer, Grading> finder = new Finder<Integer, Grading>(Grading.class);
  
  // FIXME: id -> student.id + exercise.id
  @Id
  public int id;
  
  @ManyToOne
  public Student student;
  
  @ManyToOne
  public HtmlExercise exercise;
  
  @Required
  public int points;
  
}
