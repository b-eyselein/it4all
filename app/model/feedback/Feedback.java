package model.feedback;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Feedback extends Model {
  
  @Id
  public int id;
  
  public int sinn;
  
  public int nutzen;
  
  public Note bedienung;
  
  public Note feedback;
  
  public Note korrektur;
  
  public String kommentar;
  
  public enum Note {
    NO_FEEDBACK, SEHR_GUT, GUT, EHER_SCHLECHT, SCHLECHT;
  }
  
}
