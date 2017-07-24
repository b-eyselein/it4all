package model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ProgSampleKey implements Serializable {

  private static final long serialVersionUID = 9098757966182402773L;

  private int exerciseId;

  private AvailableLanguages language;

  public ProgSampleKey(int theExerciseId, AvailableLanguages theLanguage) {
    exerciseId = theExerciseId;
    language = theLanguage;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ProgSampleKey && hashCode() == obj.hashCode();
  }

  public int getExerciseId() {
    return exerciseId;
  }

  public AvailableLanguages getLanguage() {
    return language;
  }

  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * exerciseId + language.ordinal();
  }

}