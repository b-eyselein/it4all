package model.correctionresult;

import java.util.List;
import java.util.stream.Collectors;

import model.matcher.BinaryExpressionMatch;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class WhereComparison extends MatchingResult<BinaryExpression, BinaryExpressionMatch> {

  public WhereComparison(List<BinaryExpressionMatch> theMatches, List<BinaryExpression> theNotMatchedInFirst,
      List<BinaryExpression> theNotMatchedInSecond) {
    super(theMatches, theNotMatchedInFirst, theNotMatchedInSecond);
  }

  private static <T> String concatElementsAsList(List<T> elements) {
    return elements.stream().map(T::toString).collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
  }

  public String getAsHtml() {
    String ret = "<div class=\"col-md-12\">";
    ret += "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Bedingungen</div>";
    ret += "<div class=\"panel-body\">";

    for(BinaryExpressionMatch match: matches)
      ret += match.getAsHtml();

    if(!wrong.isEmpty()) {
      ret += "<div class=\"col-md-12\">";
      ret += "<div class=\"alert alert-warning\">Folgende Bedingungen ihrer Query konnten nicht in der Musterlösung gefunden werden:";
      ret += concatElementsAsList(wrong);
      ret += "</div></div>";
    }

    if(!missing.isEmpty()) {
      ret += "<div class=\"col-md-12\">";
      ret += "<div class=\"alert alert-danger\">Folgende Bedingungen sind in der Musterlösung aber nicht in ihrer Lösung vorhanden:";
      ret += concatElementsAsList(missing);
      ret += "</div></div>";
    }

    ret += "</div></div></div>";
    return ret;
  }

}
