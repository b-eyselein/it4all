package model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class QuestionAnswer extends Model {

  @ManyToOne
  public QuestionUser user;

}
