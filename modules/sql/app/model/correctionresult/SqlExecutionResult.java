package model.correctionresult;

import model.exercise.Success;
import model.result.EvaluationResult;
import model.sql.SqlQueryResult;

public class SqlExecutionResult extends EvaluationResult {

  private SqlQueryResult userResult;
  private SqlQueryResult sampleResult;

  public SqlExecutionResult(SqlQueryResult theUserResult, SqlQueryResult theSampleResult) {
    super(analyze(theUserResult, theSampleResult));
    userResult = theUserResult;
    sampleResult = theSampleResult;
  }

  private static Success analyze(SqlQueryResult userResult, SqlQueryResult sampleResult) {
    if(userResult == null || sampleResult == null)
      return Success.FAILURE;
    else if(!userResult.isIdentic(sampleResult))
      return Success.NONE;
    else
      return Success.COMPLETE;
  }

  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }

  public SqlQueryResult getUserResult() {
    return userResult;
  }

}