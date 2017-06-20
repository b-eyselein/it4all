package model.querycorrectors.change;

import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import play.twirl.api.Html;

public class InsertResult extends SqlResult<Insert, Column> {

  protected InsertResult(String theLearnerSolution) {
    super(theLearnerSolution);
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public SqlResult<Insert, Column> makeOtherComparisons(Insert userQ, Insert sampleQ) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Html render() {
    return views.html.resultTemplates.insertResult.render(this);
  }

}
