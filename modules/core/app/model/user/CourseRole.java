package model.user;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class CourseRole extends Model {

  public static final Finder<CourseRoleKey, CourseRole> finder = new Finder<>(CourseRole.class);

  @EmbeddedId
  public CourseRoleKey key;

  @ManyToOne
  @JoinColumn(name = "course_id", insertable = false, updatable = false)
  public Course course;

  @ManyToOne
  @JoinColumn(name = "user_name", insertable = false, updatable = false)
  public User user;

  public Role role = Role.USER;

  public CourseRole(CourseRoleKey theKey) {
    key = theKey;
  }

  public Course getCourse() {
    return course;
  }

  public CourseRoleKey getKey() {
    return key;
  }

  public Role getRole() {
    return role;
  }

  public User getUser() {
    return user;
  }

  public boolean isAdmin() {
    return role == Role.ADMIN;
  }

  public void setKey(CourseRoleKey theKey) {
    key = theKey;
  }

  public void setRole(Role theRole) {
    role = theRole;
  }

}
