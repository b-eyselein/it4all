package model.querycorrectors.change;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update> {
  
  public UpdateCorrector() {
    super("UPDATE");
  }
  
  @Override
  protected MatchingResult<Column, ColumnMatch<Column>> compareColumns(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected List<String> getTableNames(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
  }
  
  @Override
  protected List<Table> getTables(Update query) {
    return query.getTables();
  }

  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
  }
  
  @Override
  protected SqlResult<Update, Column> instantiateResult(String learnerSolution) {
    return new UpdateResult(learnerSolution);
  }
  
}
