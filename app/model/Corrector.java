package model;

import java.util.List;

public abstract class Corrector<T extends Exercise> {
  
  public abstract List<String> correct(String solution, Exercise exercise);
  
}
