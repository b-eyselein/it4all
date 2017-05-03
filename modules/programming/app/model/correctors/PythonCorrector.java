package model.correctors;

import java.util.List;

import model.ProgLanguage;

public class PythonCorrector extends ProgLangCorrector {
  
  public PythonCorrector() {
    super(ProgLanguage.PYTHON);
  }
  
  @Override
  public String buildToEvaluate(String functionname, List<String> inputs) {
    return functionname + "(" + String.join(", ", inputs) + ")";
  }
  
  @Override
  protected boolean validateResult(Object realResult, Object awaitedResult) {
    // TODO Auto-generated method stub
    return false;
  }
  
}
