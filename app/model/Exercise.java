package model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class Exercise extends Model {
  
  public static Finder<Integer, Exercise> finder = new Finder<Integer, Exercise>(Exercise.class);
  
  @Id
  public int id;
  
  @Required
  public String title;
  
  @Required
  public String text;
  
  @OneToMany(mappedBy = "exercise")
  public List<SubExercise> subExercises;
  
  // TODO: check!
  public String defaultSolution;
  
  public List<String> getDefaultInLines() {
    return Arrays.asList(defaultSolution.split("\\\\n"));
  }
  
}
