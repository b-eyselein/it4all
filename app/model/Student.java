package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class Student extends Model {
  
  @Id
  public String name;
  
  @OneToMany(mappedBy="student")
  public List<Grading> gradings;
  
  public static Finder<String, Student> find = new Finder<String, Student>(Student.class);
  
}
