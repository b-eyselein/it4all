package model.javascript;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import play.data.validation.Constraints.Required;

@Entity
public class JsTestvalue extends Model {
  
  @Id
  public int id;

  @Required
  public String value;

  @ManyToOne
  @JsonBackReference
  public JsTest test;

}
