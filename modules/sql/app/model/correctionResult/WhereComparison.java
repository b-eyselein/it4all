package model.correctionResult;

import java.util.List;
import java.util.stream.Collectors;

import model.matching.Match;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class WhereComparison extends MatchingResult<BinaryExpression> {

  public WhereComparison(List<Match<BinaryExpression>> theMatches, List<BinaryExpression> theNotMatchedInFirst,
      List<BinaryExpression> theNotMatchedInSecond) {
    // TODO Auto-generated constructor stub
    super(theMatches, theNotMatchedInFirst, theNotMatchedInSecond);
  }

  @Override
  public String getAsHtml() {
    // TODO Auto-generated method stub
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Bedingungen</div>";
    ret += "<div class=\"panel-body\">";

    for(Match<BinaryExpression> match: matches)
      ret += match.getAsHtml();

    if(!notMatchedInFirst.isEmpty()) {
      ret += "<div class=\"alert alert-warning\">Folgende Bedingungen ihrer Query konnten nicht in der Musterlösung gefunden werden:";
      ret += notMatchedInFirst.stream().map(exp -> exp.toString())
          .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
      ret += "</div>";
    }

    if(!notMatchedInSecond.isEmpty()) {
      ret += "<div class=\"alert alert-danger\">Folgende Bedingungen sind in der Musterlösung aber nicht in ihrer Lösung vorhanden:";
      ret += notMatchedInSecond.stream().map(exp -> exp.toString())
          .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
      ret += "</div>";
    }

    ret += "</div>";
    ret += "</div>";
    return ret;
  }

}
