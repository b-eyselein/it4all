package model.correctionResult;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.matching.Match;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class WhereComparison extends MatchingResult<BinaryExpression> {
  
  private static <T> String concatElementsAsList(List<T> elements) {
    return elements.stream().map(el -> el.toString())
        .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"));
  }
  
  public WhereComparison(List<Match<BinaryExpression>> theMatches, List<BinaryExpression> theNotMatchedInFirst,
      List<BinaryExpression> theNotMatchedInSecond) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, theMatches, theNotMatchedInFirst, theNotMatchedInSecond);
  }
  
  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich der Bedingungen</div>";
    ret += "<div class=\"panel-body\">";
    
    for(Match<BinaryExpression> match: matches)
      ret += match.getAsHtml();
    
    if(!notMatchedInFirst.isEmpty()) {
      ret += "<div class=\"alert alert-warning\">Folgende Bedingungen ihrer Query konnten nicht in der Musterlösung gefunden werden:";
      ret += concatElementsAsList(notMatchedInFirst);
      ret += "</div>";
    }
    
    if(!notMatchedInSecond.isEmpty()) {
      ret += "<div class=\"alert alert-danger\">Folgende Bedingungen sind in der Musterlösung aber nicht in ihrer Lösung vorhanden:";
      ret += concatElementsAsList(notMatchedInSecond);
      ret += "</div>";
    }
    
    ret += "</div></div>";
    return ret;
  }
  
}
