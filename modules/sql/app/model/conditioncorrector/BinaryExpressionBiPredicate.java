package model.conditioncorrector;

import java.util.Map;
import java.util.function.BiPredicate;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionBiPredicate implements BiPredicate<BinaryExpression, BinaryExpression> {

  private Map<String, String> userTableAliases;
  private Map<String, String> sampleTableAliases;

  public BinaryExpressionBiPredicate(Map<String, String> theUserTableAliases,
      Map<String, String> theSampleTableAliases) {
    userTableAliases = theUserTableAliases;
    sampleTableAliases = theSampleTableAliases;
  }

  @Override
  public boolean test(BinaryExpression binEx1, BinaryExpression binEx2) {
    Column completeColumn1 = getColumnToCompare(binEx1);
    Column completeColumn2 = getColumnToCompare(binEx2);

    String column1 = completeColumn1.getColumnName();
    String column2 = completeColumn2.getColumnName();

    String tableAlias1 = completeColumn1.getTable().getName();
    String tableAlias2 = completeColumn2.getTable().getName();

    String table1 = tableAlias1 != null ? userTableAliases.getOrDefault(tableAlias1, tableAlias1) : "";
    String table2 = tableAlias2 != null ? sampleTableAliases.getOrDefault(tableAlias2, tableAlias2) : "";

    return column1.equals(column2) && table1.equals(table2);
  }

  private boolean compareLeftExpression(BinaryExpression expression) {
    Expression leftExp = expression.getLeftExpression();
    Expression rightExp = expression.getRightExpression();

    return !(rightExp instanceof Column)
        || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
  }

  private Column getColumnToCompare(BinaryExpression expression) {
    return compareLeftExpression(expression) ? (Column) expression.getLeftExpression()
        : (Column) expression.getRightExpression();
  }

}
