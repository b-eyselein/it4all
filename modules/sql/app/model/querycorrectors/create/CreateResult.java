package model.querycorrectors.create;

import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.twirl.api.Html;

public class CreateResult extends SqlResult<CreateTable, ColumnDefinition> {
  
  protected CreateResult(String theLearnerSolution) {
    super(theLearnerSolution);
    // TODO Auto-generated constructor stub
  }

  @Override
  public SqlResult<CreateTable, ColumnDefinition> makeOtherComparisons(CreateTable userQ, CreateTable sampleQ) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Html render() {
    return views.html.resultTemplates.createResult.render(this);
  }
  
}
