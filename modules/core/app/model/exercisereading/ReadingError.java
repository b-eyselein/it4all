package model.exercisereading;

import java.util.List;

import com.github.fge.jsonschema.core.report.ProcessingMessage;

import model.exercise.Exercise;

public class ReadingError<T extends Exercise> extends AbstractReadingResult<T> {
  
  private List<ProcessingMessage> causes;
  
  public ReadingError(String theJson, List<ProcessingMessage> theCauses) {
    super(theJson);
    causes = theCauses;
  }
  
  public List<ProcessingMessage> getCauses() {
    return causes;
  }
  
  @Override
  public boolean isSuccess() {
    return false;
  }
  
}
