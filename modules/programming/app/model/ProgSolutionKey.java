package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ProgSolutionKey implements Serializable {
  
  private static final long serialVersionUID = -986468474L;
  
  public String userName;
  
  public int exerciseId;
  
  public AvailableLanguages language;
  
  public ProgSolutionKey(String theUserName, int theExerciseId, AvailableLanguages theLanguage) {
    userName = theUserName;
    exerciseId = theExerciseId;
    language = theLanguage;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof ProgSolutionKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.MILLION * userName.hashCode() + IntConsts.THOUSAND * exerciseId + language.ordinal();
  }
  
}
