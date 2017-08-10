package model.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.Finder;
import io.ebean.Model;
import model.WithId;

@Entity
public class Course extends Model implements WithId {

  public static final Finder<Integer, Course> finder = new Finder<>(Course.class);
  
  @Id
  public int id;
  
  public String name;
  
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
  public List<CourseRole> courseRoles;
  
  public Course(int theId) {
    id = theId;
  }
  
  public List<String> getAdministratorNames() {
    List<String> admins = courseRoles.parallelStream().filter(CourseRole::isAdmin).map(cr -> cr.user.name)
        .collect(Collectors.toList());
    
    if(admins.isEmpty())
      return Arrays.asList("-- Dieser Kurs besitzt noch keine Administratoren! --");
    
    return admins;
  }
  
  @Override
  public int getId() {
    return id;
  }
  
  public List<User> getUsers() {
    return courseRoles.parallelStream().map(CourseRole::getUser).collect(Collectors.toList());
  }
  
}
