package model.querycorrectors.update;

import java.util.Arrays;
import java.util.List;

import model.SqlCorrectionException;
import model.matching.MatchingResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends ChangeCorrector<Delete, Delete> {

  @Override
  protected MatchingResult<String> compareColumns(Delete userQuery, Delete sampleQuery) {
    return null;
  }
  
  @Override
  protected MatchingResult<String> compareGroupByElements(Delete userQuery, Delete sampleQuery) {
    return null;
  }

  @Override
  protected MatchingResult<String> compareOrderByElements(Delete userQuery, Delete sampleQuery) {
    return null;
  }
  
  @Override
  protected Delete getPlainStatement(Delete statement) {
    return statement;
  }

  @Override
  protected List<String> getTables(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }

  @Override
  protected Delete parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Delete) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) { // NOSONAR
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) { // NOSONAR
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde DELETE!");
    }
  }
}
