package model.user;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CourseRoleKey implements Serializable {

  private static final long serialVersionUID = 7460272842866434669L;

  public String userName;

  public int courseId;

  public CourseRoleKey(String theUserName, int theCourseId) {
    userName = theUserName;
    courseId = theCourseId;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CourseRoleKey && hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return 1_000_000 * userName.hashCode() + courseId;
  }

}
