package model.correctionresult;

import model.SqlQueryResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class SqlExecutionResult extends EvaluationResult {
  
  private SqlQueryResult userResult;
  private SqlQueryResult sampleResult;
  
  public SqlExecutionResult(FeedbackLevel theFeedbackLevel, SqlQueryResult theUserResult,
      SqlQueryResult theSampleResult) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, analyze(theUserResult, theSampleResult));
    requestedFL = theFeedbackLevel;
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