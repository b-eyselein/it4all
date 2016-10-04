package model.correctionResult;

import java.util.List;
import java.util.stream.Collectors;

import model.result.Match;
import model.result.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class WhereComparison extends MatchingResult<BinaryExpression> {
  
  private List<Match<BinaryExpression>> matches;
  
  private List<BinaryExpression> notMatchedInUser, notMatchedInSample;
  
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
