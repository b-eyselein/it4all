package model.uml;

import javax.persistence.Entity;

import io.ebean.Model;

@Entity
public class UmlImplementation extends Model {

  private String subClass;

  private String superClass;

  public UmlImplementation() {
    // Dummy constructor for Json.fromJson(...)
  }

  public UmlImplementation(String theSubClass, String theSuperClass) {
    subClass = theSubClass;
    superClass = theSuperClass;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof UmlImplementation))
      return false;
    UmlImplementation other = (UmlImplementation) obj;
    return subClass.equals(other.subClass) && superClass.equals(other.superClass);
  }

  public String getSubClass() {
    return subClass;
  }

  public String getSuperClass() {
    return superClass;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((subClass == null) ? 0 : subClass.hashCode());
    result = prime * result + ((superClass == null) ? 0 : superClass.hashCode());
    return result;
  }

  public void setSubClass(String theSubClass) {
    subClass = theSubClass;
  }

  public void setSuperClass(String theSuperClass) {
    superClass = theSuperClass;
  }

}
