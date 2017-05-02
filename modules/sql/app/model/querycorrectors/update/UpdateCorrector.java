package model.querycorrectors.update;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.correctionresult.ComparisonTwoListsOfStrings;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;
import play.Logger;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update, Update> {

  private List<String> getColumns(Update statement) {
    List<Column> columns = statement.getColumns();
    return (columns == null) ? Collections.emptyList()
        : columns.stream().map(Column::getColumnName).collect(Collectors.toList());
  }

  @Override
  protected ComparisonTwoListsOfStrings compareColumns(Update userQuery, Update sampleQuery) {
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    
    // FIXME: correct set ... part of query!
    for(Expression e: userQuery.getExpressions())
      Logger.debug(e + "");
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    return new ComparisonTwoListsOfStrings("Spalten", missingColumns, wrongColumns);
  }

  @Override
  protected ComparisonTwoListsOfStrings compareGroupByElements(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected ComparisonTwoListsOfStrings compareOrderByElements(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Update getPlainStatement(Update query) {
    return query;
  }

  @Override
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
  }

  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
  }

  @Override
  protected Update parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Update) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde UPDATE!");
    }
  }

}
