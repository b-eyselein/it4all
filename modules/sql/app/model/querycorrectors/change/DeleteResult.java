package model.querycorrectors.change;

import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;
import play.twirl.api.Html;

public class DeleteResult extends SqlResult<Delete, Column> {

  protected DeleteResult(String theLearnerSolution) {
    super(theLearnerSolution);
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public SqlResult<Delete, Column> makeOtherComparisons(Delete userQ, Delete sampleQ) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Html render() {
    return views.html.resultTemplates.deleteResult.render(this);
  }

}
