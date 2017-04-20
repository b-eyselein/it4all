package model.uml;

public class UmlImplementation {

  public String subClass;
  
  public String superClass;
  
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof UmlImplementation))
      return false;
    UmlImplementation other = (UmlImplementation) obj;
    return subClass.equals(other.subClass) && superClass.equals(other.superClass);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((subClass == null) ? 0 : subClass.hashCode());
    result = prime * result + ((superClass == null) ? 0 : superClass.hashCode());
    return result;
  }

}
