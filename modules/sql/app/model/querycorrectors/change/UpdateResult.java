package model.querycorrectors.change;

import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import play.twirl.api.Html;

public class UpdateResult extends SqlResult<Update, Column> {

  protected UpdateResult(String theLearnerSolution) {
    super(theLearnerSolution);
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public SqlResult<Update, Column> makeOtherComparisons(Update userQ, Update sampleQ) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Html render() {
    return views.html.resultTemplates.updateResult.render(this);
  }

}
