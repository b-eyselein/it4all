package model.querycorrectors.select;

import model.matching.MatchType;
import model.querycorrectors.ColumnMatch;
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

  private static boolean compareAliases(Alias alias1, Alias alias2) {
    return alias1 != null && alias2 != null && alias1.getName().equals(alias2.getName());
  }

  private static String getAlias(SelectItem item) {
    if(item instanceof AllColumns || item instanceof AllTableColumns)
      return "";
    Alias alias = ((SelectExpressionItem) item).getAlias();
    if(alias == null)
      return "";
    return alias.toString();
  }

  private static String getColName(SelectItem item) {
    if(item instanceof AllColumns)
      return "*";
    else if(item instanceof AllTableColumns)
      return ((AllTableColumns) item).toString();
    else
      return ((SelectExpressionItem) item).getExpression().toString();
  }

  @Override
  public boolean aliasesMatched() {
    return aliasesEqual;
  }

  @Override
  public boolean colNamesMatched() {
    return matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH;
  }

  @Override
  public String getFirstColAlias() {
    return getAlias(userArg);
  }

  @Override
  public String getFirstColName() {
    return getColName(userArg);
  }

  @Override
  public String getSecondColAlias() {
    return getAlias(sampleArg);
  }

  @Override
  public String getSecondColName() {
    return getColName(sampleArg);
  }

  @Override
  public boolean hasAlias() {
    return aliasesCompared;
  }

  @Override
  protected MatchType analyze(SelectItem theArg1, SelectItem theArg2) {
    if((theArg1 instanceof AllColumns && theArg2 instanceof AllColumns)
        || (theArg1 instanceof AllTableColumns && theArg2 instanceof AllTableColumns))
      return MatchType.SUCCESSFUL_MATCH;

    if(!(theArg1 instanceof SelectExpressionItem) || !(theArg2 instanceof SelectExpressionItem))
      return MatchType.UNSUCCESSFUL_MATCH;

    SelectExpressionItem selExpr1 = (SelectExpressionItem) theArg1;
    SelectExpressionItem selExpr2 = (SelectExpressionItem) theArg2;

    aliasesCompared = selExpr1.getAlias() != null || selExpr2.getAlias() != null;

    if(aliasesCompared)
      aliasesEqual = compareAliases(selExpr1.getAlias(), selExpr2.getAlias());

    if(!aliasesCompared || aliasesEqual)
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }
}
