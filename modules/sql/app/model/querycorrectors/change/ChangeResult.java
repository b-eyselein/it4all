package model.querycorrectors.change;

import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import play.twirl.api.Html;

public class ChangeResult<Q extends Statement> extends SqlResult<Q, Column> {
  
  public ChangeResult(String theLearnerSolution) {
    super(theLearnerSolution);
  }

  @Override
  public SqlResult<Q, Column> makeOtherComparisons(Q userQ, Q sampleQ) {
    return this;
  }

  @Override
  public Html render() {
    return views.html.resultTemplates.changeResult.render(this);
  }
  
}
