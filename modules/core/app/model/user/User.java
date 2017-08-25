package model.user;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
@Table(name = "users")
public class User extends Model {
  
  public enum SHOW_HIDE_AGGREGATE {
    SHOW("Einblenden"), AGGREGATE("Zusammenfassen"), HIDE("Ausblenden");
    
    private String german;
    
    private SHOW_HIDE_AGGREGATE(String theGerman) {
      german = theGerman;
    }
    
    public String getGerman() {
      return german;
    }
  }
  
  public static final Finder<String, User> finder = new Finder<>(User.class);
  
  @Id
  public String name;
  
  @Enumerated(EnumType.STRING)
  public Role stdRole = Role.USER;
  
  @OneToMany(mappedBy = "user")
  @JsonIgnore
  public List<CourseRole> courseRoles;
  
  @Enumerated(EnumType.STRING)
  public SHOW_HIDE_AGGREGATE todo = SHOW_HIDE_AGGREGATE.SHOW;
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof User && ((User) obj).name.equals(name);
  }
  
  @JsonIgnore
  public List<String> getCourseNames() {
    return courseRoles.parallelStream().map(cr -> cr.course.name).collect(Collectors.toList());
  }
  
  @JsonIgnore
  public List<Course> getCourses() {
    return courseRoles.parallelStream().map(cr -> cr.course).collect(Collectors.toList());
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }
  
  @JsonIgnore
  public boolean isAdmin() {
    return stdRole.isAdminRole();
  }
  
  public boolean isAdminInCourse(Course course) {
    CourseRole courseRole = CourseRole.finder.byId(new CourseRoleKey(name, course.getId()));
    return courseRole != null && courseRole.role.isAdminRole();
  }
  
  public boolean isInCourse(Course course) {
    return CourseRole.finder.byId(new CourseRoleKey(name, course.getId())) != null;
  }
  
  public void setTodo(SHOW_HIDE_AGGREGATE newTodo) {
    todo = newTodo;
  }
  
}
