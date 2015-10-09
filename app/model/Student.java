package model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Student extends Model {
  
  @Id
  public String name;
  
  public static Finder<String, Student> find = new Finder<String, Student>(Student.class);
  
  public static Student authenticate(String name, String password) {
    // TODO Auto-generated method stub
    return find.byId(name);
  }
  
}
