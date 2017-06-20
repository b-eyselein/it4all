package model.querycorrectors.select;

import model.querycorrectors.ColumnMatch;
import model.querycorrectors.ColumnMatcher;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SelectColumnMatcher extends ColumnMatcher<SelectItem> {

  public SelectColumnMatcher() {
    super((selItem1, selItem2) -> {
      if(selItem1 instanceof AllColumns) {
        return selItem2 instanceof AllColumns;

      } else if(selItem1 instanceof AllTableColumns) {
        return selItem2 instanceof AllTableColumns
            && tableNamesEqual((AllTableColumns) selItem1, (AllTableColumns) selItem2);

      } else if(selItem1 instanceof SelectExpressionItem) {
        return selItem2 instanceof SelectExpressionItem
            && selectExprEqual((SelectExpressionItem) selItem1, (SelectExpressionItem) selItem2);
      } else {
        return false;
      }
    });
  }

  private static boolean selectExprEqual(SelectExpressionItem selExprItem, SelectExpressionItem selExprItem2) {
    return selExprItem.getExpression().toString().equalsIgnoreCase(selExprItem2.getExpression().toString());
  }

  private static boolean tableNamesEqual(AllTableColumns col1, AllTableColumns col2) {
    return col1.getTable().getFullyQualifiedName().equals(col2.getTable().getFullyQualifiedName());
  }

  @Override
  protected ColumnMatch<SelectItem> instantiateMatch(SelectItem arg1, SelectItem arg2) {
    return new SelectColumnMatch(arg1, arg2);
  }

}
