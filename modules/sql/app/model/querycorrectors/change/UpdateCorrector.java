package model.querycorrectors.change;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.columnmatch.ColumnMatch;
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
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
  }
  
  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
  }
  
}
