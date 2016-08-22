package model.spread;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class SpreadExercise extends Model {
  
  public static Finder<Integer, SpreadExercise> finder = new Finder<>(SpreadExercise.class);
  
  @Id
  public int id;
  
  public String title;
  
  @Column(columnDefinition = "text")
  public String text;
  
  public String sampleFilename;

  public String templateFilename;
  
  public SpreadExercise(int theId) {
    id = theId;
  }
}
