package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public class GroupByComparison extends EvaluationResult {

  private static Success analyze(List<String> theMissing, List<String> theUnnecessary) {
    if(theMissing.isEmpty() && theUnnecessary.isEmpty())
      return Success.COMPLETE;
    else if(theMissing.isEmpty())
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  List<String> missing, unnecessary;

  public GroupByComparison(List<String> theMissing, List<String> theUnnecessary) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, analyze(theMissing, theUnnecessary));
    missing = theMissing;
    unnecessary = theUnnecessary;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Order By-Elemente</div>";
    ret += "<div class=\"panel-body\">";

    if(missing.isEmpty() && unnecessary.isEmpty())
      ret += "<p>Alle Order By-Elemente stimmen.</p>";

    if(!missing.isEmpty())
      ret += "<p>Es fehlen folgende Group By-Elemente: " + concatCodeElements(missing) + "</p>";

    if(!unnecessary.isEmpty())
      ret += "<p>Folgende Group By-Elemente sind nicht nötig: " + concatCodeElements(unnecessary) + "</p>";

    ret += "</div></div>";
    return ret;
  }
}
