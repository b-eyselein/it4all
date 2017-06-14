package model.querycorrectors.select;

import model.querycorrectors.columnMatch.ColumnMatch;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class SelectColumnMatch extends ColumnMatch<SelectItem> {

  private boolean aliasesCompared;
  private boolean aliasesEqual;

  public SelectColumnMatch(SelectItem theArg1, SelectItem theArg2) {
    super(theArg1, theArg2);
  }

  private static boolean compareSelectExpressions(SelectExpressionItem selectExpr1, SelectExpressionItem selectExpr2) {
    Alias alias1 = selectExpr1.getAlias();
    return alias1 != null && alias1.getName().equals(selectExpr2.getAlias().getName());
  }

  @Override
  public String describe() {
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append("<p>Nutzer: <code>" + arg1.toString() + "</code></p>");
    strBuilder.append("<p>Muster: <code>" + arg2.toString() + "</code></p>");

    if(aliasesCompared)
      strBuilder.append("<p>Die Aliasse stimmen " + (aliasesEqual ? "" : "<strong>nicht</strong> ") + "überein.</p>");

    return strBuilder.toString();
  }

  @Override
  protected boolean analyze(SelectItem theArg1, SelectItem theArg2) {
    if((theArg1 instanceof AllColumns && theArg2 instanceof AllColumns)
        || (theArg1 instanceof AllTableColumns && theArg2 instanceof AllTableColumns))
      return true;

    if(!(theArg1 instanceof SelectExpressionItem) || !(theArg2 instanceof SelectExpressionItem))
      return false;

    SelectExpressionItem selExpr1 = (SelectExpressionItem) theArg1;
    SelectExpressionItem selExpr2 = (SelectExpressionItem) theArg2;

    aliasesCompared = selExpr2.getAlias() != null;

    if(aliasesCompared)
      aliasesEqual = compareSelectExpressions(selExpr1, selExpr2);

    return aliasesEqual;
  }
}
