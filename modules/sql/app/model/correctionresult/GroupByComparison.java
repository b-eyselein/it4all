package model.correctionresult;

import java.util.List;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class GroupByComparison extends EvaluationResult {

  List<String> missing;

  List<String> unnecessary;

  public GroupByComparison(List<String> theMissing, List<String> theUnnecessary) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, analyze(theMissing, theUnnecessary));
    missing = theMissing;
    unnecessary = theUnnecessary;
  }

  // FIXME: implement feedbackLevel!
  private static Success analyze(List<String> theMissing, List<String> theUnnecessary) {
    if(theMissing.isEmpty() && theUnnecessary.isEmpty())
      return Success.COMPLETE;
    else if(theMissing.isEmpty())
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"col-md-6\">";
    ret += "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Order By-Elemente</div>";
    ret += "<div class=\"panel-body\">";

    if(missing.isEmpty() && unnecessary.isEmpty())
      ret += "<p>Alle Order By-Elemente stimmen.</p>";
    else
      ret += "<p>Es fehlen folgende Group By-Elemente: " + concatCodeElements(missing) + "</p>"
          + "<p>Folgende Group By-Elemente sind nicht nötig: " + concatCodeElements(unnecessary) + "</p>";

    ret += "</div></div></div>";
    return ret;
  }
}
