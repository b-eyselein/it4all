package model.correctionResult;

import model.SqlQueryResult;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class SqlExecutionResult extends EvaluationResult {
  
  // FIXME: implement feedbackLevel!
  private String message;
  
  private SqlQueryResult userResult;
  private SqlQueryResult sampleResult;
  
  public SqlExecutionResult(FeedbackLevel theFeedbackLevel, SqlQueryResult theUserResult,
      SqlQueryResult theSampleResult) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE);
    requestedFL = theFeedbackLevel;
    userResult = theUserResult;
    sampleResult = theSampleResult;
    
    analyze();
  }
  
  @Override
  public String getAsHtml() {
    String ret = "<div class=\"col-md-12\">";
    ret += "<div class=\"alert alert-success\">Ihre Query wurde erfolgreich korrigiert.</div>";
    
    if(requestedFL.compareTo(FeedbackLevel.MINIMAL_FEEDBACK) >= 0)
      ret += "<div class=\"alert alert-" + getBSClass() + "\"><p>" + message + "</p></div>";
    
    if(requestedFL.compareTo(FeedbackLevel.MEDIUM_FEEDBACK) >= 0)
      ret += resultsAsHtml();
    ret += "</div>";
    return ret;
  }
  
  public String getMessage() {
    return message;
  }
  
  public SqlQueryResult getSampleResult() {
    return sampleResult;
  }
  
  public SqlQueryResult getUserResult() {
    return userResult;
  }
  
  private void analyze() {
    if(userResult == null || sampleResult == null) {
      message = "Es gab ein Problem beim Ausführen einer oder beider Queries!";
      return;
    }
    if(userResult.isIdentic(sampleResult)) {
      success = Success.COMPLETE;
      message = "Resultat stimmt mit der Musterlösung überein.";
    } else {
      success = Success.NONE;
      message = "Resultat stimmt nicht mit der Musterlösung überein!";
    }
  }
  
  private String resultsAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich ihrer L&ouml;sung mit der Musterl&ouml;sung</div>";
    
    ret += "<div class=\"panel-body\">";
    if(success == Success.COMPLETE) {
      // Results are identical
      ret += "<p>Ihre L&ouml;sung == Musterl&ouml;sung</p>" + userResult.toHtmlTable();
    } else {
      // Results are not identical
      ret += "<div class=\"col-md-6\"><p>Ihre L&ouml;sung:</p>";
      if(userResult == null)
        ret += "Es gab einen Fehler bei der Ausf&uuml;hrung ihrer L&ouml;sung!";
      else
        ret += userResult.toHtmlTable();
      ret += "</div>";
      
      ret += "<div class=\"col-md-6\"><p>Musterl&ouml;sung:</p>";
      if(sampleResult != null)
        ret += sampleResult.toHtmlTable();
      ret += "</div>";
    }
    ret += "</div>";
    
    ret += "</div>";
    return ret;
  }
  
}