package model.querycorrectors.select;

import model.matching.Match;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SelectColumnMatch extends Match<SelectItem> {

  public SelectColumnMatch(SelectItem theArg1, SelectItem theArg2) {
    super(theArg1, theArg2);
  }
  
  @Override
  protected boolean analyze(SelectItem theArg1, SelectItem theArg2) {
    // TODO Auto-generated method stub
    return false;
  }
}
