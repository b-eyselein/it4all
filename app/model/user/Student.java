package model.user;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import model.Grading;

import com.avaje.ebean.Model;

@Entity
public class Student extends Model implements User {
  
  @Id
  public String name;
  
  @OneToMany(mappedBy = "student")
  public List<Grading> gradings;
  
  public static Finder<String, Student> find = new Finder<String, Student>(Student.class);

  @Override
  public String getName() {
    return name;
  }
  
}
