package model.css;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class CssExercise extends Model {
  
  public static Finder<Integer, CssExercise> finder = new Finder<Integer, CssExercise>(CssExercise.class);

  @Id
  public int id;

  public String title;

  @Column(name = "exerciseText", length = 2000)
  public String exerciseText;

}
