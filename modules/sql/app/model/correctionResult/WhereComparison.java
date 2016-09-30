package model.correctionResult;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.result.Match;
import model.result.Matcher;
import net.sf.jsqlparser.expression.BinaryExpression;

public class WhereComparison extends EvaluationResult {

  private static Success analyze(Matcher<BinaryExpression> matcher) {
    boolean allMatched = matcher.getNotMatchedInFirst().isEmpty() && matcher.getNotMatchedInSecond().isEmpty();

    boolean matchesOk = true;
    for(Match<BinaryExpression> match: matcher.getMatches())
      if(match.getSuccess() != Success.COMPLETE)
        matchesOk = false;

    if(allMatched && matchesOk)
      return Success.COMPLETE;
    else if(allMatched || matchesOk)
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  private List<Match<BinaryExpression>> matches;

  private List<BinaryExpression> notMatchedInUser, notMatchedInSample;

  public WhereComparison(Matcher<BinaryExpression> matcher) {
    super(analyze(matcher));
    matches = matcher.getMatches();
    notMatchedInUser = matcher.getNotMatchedInFirst();
    notMatchedInSample = matcher.getNotMatchedInSecond();
  }

  @Override
  public String getAsHtml() {
    // TODO Auto-generated method stub
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Bedingungen</div>";
    ret += "<div class=\"panel-body\">";

    for(Match<BinaryExpression> match: matches)
      ret += match.getAsHtml();

    if(!notMatchedInUser.isEmpty()) {
      ret += "<div class=\"alert alert-warning\">Folgende Bedingungen ihrer Query konnten nicht in der Musterlösung gefunden werden:";
      ret += notMatchedInUser.stream().map(exp -> exp.toString())
          .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
      ret += "</div>";
    }

    if(!notMatchedInSample.isEmpty()) {
      ret += "<div class=\"alert alert-danger\">Folgende Bedingungen sind in der Musterlösung aber nicht in ihrer Lösung vorhanden:";
      ret += notMatchedInSample.stream().map(exp -> exp.toString())
          .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
      ret += "</div>";
    }

    ret += "</div>";
    ret += "</div>";
    return ret;
  }

}
