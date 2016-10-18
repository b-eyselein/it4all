package model.mindmap.parser;

public class ParserFactory {
  
  public static AbstractEvaluationParser getEvaluationParser(String parserType) {
    switch(parserType.toUpperCase()) {
    case "FREEPLANE":
      return new FreePlanParser();
    case "MINDMANAGER":
      return new MindManagerParser();
    default:
      throw new IllegalArgumentException("Check your spelling.");
    }
  }
  
  public static AbstractParser getTOCParser(String parserType) {
    switch(parserType.toUpperCase()) {
    case "FREEPLANE":
      return new FreePlanParser();
    case "MINDMANAGER":
      return new MindManagerParser();
    case "LATEX":
      return new LatexParser();
    case "WORD":
      return new WordParser();
    default:
      throw new IllegalArgumentException("Check your spelling.");
    }
  }
  
  private ParserFactory() {

  }
}
