package model.feedback;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Feedback extends Model {
  
  public static Finder<Integer, Feedback> finder = new Finder<Integer, Feedback>(Feedback.class);
  
  @Id
  public int id;
  
  public int sinnHtml;
  public int sinnExcel;
  
  public int nutzenHtml;
  public int nutzenExcel;
  
  public Note bedienungHtml;
  public Note bedienungExcel;
  
  public Note feedbackHtml;
  public Note feedbackExcel;
  
  public Note korrekturHtml;
  public Note korrekturExcel;
  
  public String kommentarHtml;
  public String kommentarExcel;
  
  public enum Note {
    NO_FEEDBACK, SEHR_GUT, GUT, EHER_SCHLECHT, SCHLECHT;
  }
  
}
