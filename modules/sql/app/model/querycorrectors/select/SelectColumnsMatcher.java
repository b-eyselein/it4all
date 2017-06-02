package model.querycorrectors.select;

import model.matching.Match;
import model.matching.Matcher;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SelectColumnsMatcher extends Matcher<SelectItem> {
  
  public SelectColumnsMatcher() {
    super((col1, col2) -> {
      if(col1 instanceof AllColumns) {
        return col2 instanceof AllColumns;
        
      } else if(col1 instanceof AllTableColumns) {
        return col2 instanceof AllTableColumns && tableNamesEqual((AllTableColumns) col1, (AllTableColumns) col2);
        
      } else if(col1 instanceof SelectExpressionItem) {
        return col2 instanceof SelectExpressionItem
            && selectExprEqual((SelectExpressionItem) col1, (SelectExpressionItem) col2);
      } else {
        return false;
      }
    });
  }
  
  private static boolean selectExprEqual(SelectExpressionItem col1, SelectExpressionItem col2) {
    return col1.getExpression().equals(col2.getExpression());
  }
  
  private static boolean tableNamesEqual(AllTableColumns col1, AllTableColumns col2) {
    return col1.getTable().getFullyQualifiedName().equals(col2.getTable().getFullyQualifiedName());
  }
  
  @Override
  protected Match<SelectItem> instantiateMatch(SelectItem arg1, SelectItem arg2) {
    return new SelectColumnMatch(arg1, arg2);
  }
  
}
