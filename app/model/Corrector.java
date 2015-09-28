package model;

import java.util.List;

public abstract class Corrector<T extends Exercise> {
  
  protected T ex;
  
  public Corrector(T exercise) {
    ex = exercise;
  }
  
  public abstract List<String> correct(String solution);
  
}
