package model.mindmap.evaluation.enums;

import model.mindmap.parser.AbstractEvaluationParser;
import model.mindmap.parser.FreePlanParser;
import model.mindmap.parser.MindManagerParser;

public enum EvalParserType {

  FREEPLANE(new FreePlanParser()), MINDMANAGER(new MindManagerParser());
  
  private AbstractEvaluationParser parser;
  
  private EvalParserType(AbstractEvaluationParser theParser) {
    parser = theParser;
  }
  
  public AbstractEvaluationParser getEvalParser() {
    return null;
  }
  
  public AbstractEvaluationParser getParser() {
    return parser;
  }
}
