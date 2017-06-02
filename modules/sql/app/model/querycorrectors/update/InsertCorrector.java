package model.querycorrectors.update;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import model.ColumnWrapper;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.insert.Insert;

@Singleton
public class InsertCorrector extends ChangeCorrector<Insert> {

  public InsertCorrector() {
    super("INSERT", true, false);
  }
  
  // @Override
  // protected List<String> getColumns(Insert statement) {
  // List<Column> columns = statement.getColumns();
  //
  // if(columns == null)
  // return Collections.emptyList();
  //
  // return
  // columns.stream().map(Column::getColumnName).collect(Collectors.toList());
  // }

  @Override
  protected MatchingResult<ColumnWrapper<?>> compareColumns(Insert userQuery, Insert sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getTables(Insert userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }
  
  @Override
  protected Expression getWhere(Insert query) {
    throw new UnsupportedOperationException("A INSERT statement has no WHERE clauses!");
  }

}
