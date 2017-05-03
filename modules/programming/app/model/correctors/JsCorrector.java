package model.correctors;

import java.util.List;

import model.ProgLanguage;

public class JsCorrector extends ProgLangCorrector {
  
  public JsCorrector() {
    super(ProgLanguage.JS);
  }
  
  @Override
  public String buildToEvaluate(String functionname, List<String> inputs) {
    return functionname + "(" + String.join(", ", inputs) + ");";
  }
  
  @Override
  protected boolean validateResult(Object realResult, Object awaitedResult) {
    // TODO Auto-generated method stub
    return false;
  }
  
}
