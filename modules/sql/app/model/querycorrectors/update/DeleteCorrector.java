package model.querycorrectors.update;

import java.util.Arrays;
import java.util.List;

import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends ChangeCorrector<Delete> {

  @Override
  protected MatchingResult<String> compareColumns(Delete userQuery, Delete sampleQuery) {
    return null;
  }

  @Override
  protected List<String> getTables(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }

}
