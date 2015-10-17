package model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Student extends Model {
  
  @Id
  public String name;
  
  public static Finder<String, Student> find = new Finder<String, Student>(Student.class);
  
}
