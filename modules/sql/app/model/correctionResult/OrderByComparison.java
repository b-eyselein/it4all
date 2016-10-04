package model.correctionResult;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.Success;

public class OrderByComparison extends EvaluationResult {

  private static Success analyze(List<String> theMissing, List<String> theUnnecessary) {
    if(theMissing.isEmpty() && theUnnecessary.isEmpty())
      return Success.COMPLETE;
    else if(theMissing.isEmpty())
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  List<String> missing, unnecessary;

  public OrderByComparison(List<String> theMissing, List<String> theUnnecessary) {
    super(analyze(theMissing, theUnnecessary));
    missing = theMissing;
    unnecessary = theUnnecessary;
  }

  @Override
  public String getAsHtml() {
    // TODO Auto-generated method stub
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Order By-Elemente</div>";
    ret += "<div class=\"panel-body\">";

    if(missing.isEmpty() && unnecessary.isEmpty())
      ret += "<p>Alle Order By-Elemente stimmen.</p>";

    if(!missing.isEmpty())
      ret += "<p>Es fehlen folgende Order By-Elemente: <code>" + String.join("</code>, <code>", missing)
          + "</code></p>";

    if(!unnecessary.isEmpty())
      ret += "<p>Folgende Order By-Elemente sind nicht nötig: <code>" + String.join("</code>, <code>", unnecessary)
          + "</code></p>";

    ret += "</div></div>";
    return ret;
  }
}
