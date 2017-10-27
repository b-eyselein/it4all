package model.mindmap.evaluation.enums;

import model.mindmap.parser.AbstractParser;
import model.mindmap.parser.FreePlanParser;
import model.mindmap.parser.LatexParser;
import model.mindmap.parser.MindManagerParser;
import model.mindmap.parser.WordParser;

public enum ParserType {

  FREEPLANE(new FreePlanParser()), MINDMANAGER(new MindManagerParser()), LATEX(new LatexParser()), WORD(
      new WordParser());

  private AbstractParser parser;

  private ParserType(AbstractParser theParser) {
    parser = theParser;
  }

  public AbstractParser getParser() {
    return parser;
  }

}
