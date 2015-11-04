package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import model.html.ElementResult;
import model.html.ElementResultByName;
import model.html.ElementResultByTag;
import model.html.MultiElementResultByName;

import com.avaje.ebean.Model;

@Entity
public class Task extends Model {
  
  @Id
  public int id;
  
  @ManyToOne
  public Exercise exercise;
  
  public String taskDescription;
  public int pts;
  
  private ResultType resultType;
  private String tagName;
  private String elementName;
  private String attributes;
  
  public ElementResult getElementResult() {
    ElementResult result = null;
    switch(resultType) {
    case NAME:
      result = new ElementResultByName(this, tagName, elementName, attributes);
      break;
    case TAG:
      result = new ElementResultByTag(this, tagName, elementName, attributes);
      break;
    case MULTINAME:
      String[] attrs = attributes.split("#");
      result = new MultiElementResultByName(this, tagName, elementName, attrs[0], attrs[1]);
      break;
    default:
      break;
    }
    return result;
  }
  
  public enum ResultType {
    NAME, TAG, MULTINAME;
  };
  
}
