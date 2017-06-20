package model.querycorrectors.change;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

@Singleton
public class InsertCorrector extends ChangeCorrector<Insert> {

  public InsertCorrector() {
    super("INSERT");
  }

  @Override
  protected MatchingResult<Column, ColumnMatch<Column>> compareColumns(Insert userQuery, Insert sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getTableNames(Insert userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected List<Table> getTables(Insert query) {
    return Arrays.asList(query.getTable());
  }

  @Override
  protected Expression getWhere(Insert query) {
    throw new UnsupportedOperationException("A INSERT statement has no WHERE clauses!");
  }
  
  @Override
  protected SqlResult<Insert, Column> instantiateResult(String learnerSolution) {
    return new InsertResult(learnerSolution);
  }

}
