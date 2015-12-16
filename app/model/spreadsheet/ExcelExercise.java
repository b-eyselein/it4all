package model.spreadsheet;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;

import com.avaje.ebean.Model;

@Entity
public class ExcelExercise extends Model {
  
  public static Finder<Integer, ExcelExercise> finder = new Finder<Integer, ExcelExercise>(ExcelExercise.class);
  
  @Id
  public int id;
  
  @Required
  public String title;
  
  @Required
  public String fileName;
  
}
