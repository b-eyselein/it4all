package model.querycorrectors.select;

import model.ColumnWrapper;
import model.matching.Matcher;
import net.sf.jsqlparser.statement.select.SelectItem;
import play.Logger;

public class SelectColumnsMatcher extends Matcher<ColumnWrapper<SelectItem>> {

  public SelectColumnsMatcher() {
    super((col1, col2) -> {
      // TODO Auto-generated constructor stub
      Logger.debug(col1.getClass().getName());
      Logger.debug(col2.getClass().getName());
      return false;
    });
  }

}
