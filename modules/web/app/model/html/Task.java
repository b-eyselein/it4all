package model.html;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class Task extends Model {
  
  public enum ResultType {
    NAME, TAG, MULTINAME;
  }
  
  @Id
  public int id;
  
  @ManyToOne
  public HtmlExercise exercise;
  public String taskDescription;
  
  public int pts;
  private ResultType resultType;
  private String tagName;
  private String elementName;
  
  private String attributes;
  
  public ElementResult getElementResult() {
    switch(resultType) {
    case NAME:
      return new ElementResultByName(this, tagName, elementName, attributes);
    case TAG:
      return new ElementResultByTag(this, tagName, attributes);
    case MULTINAME:
      String[] attrs = attributes.split("#");
      return new MultiElementResultByName(this, tagName, elementName, attrs[0], attrs[1]);
    default:
      return null;
    }
  };
  
}
