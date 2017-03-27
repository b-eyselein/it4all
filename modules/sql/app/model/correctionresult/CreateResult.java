package model.correctionresult;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.matcher.ColumnDefinitionMatch;
import model.matching.MatchingResult;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class CreateResult extends MatchingResult<ColumnDefinition, ColumnDefinitionMatch> {

  public CreateResult(List<ColumnDefinitionMatch> theMatches, List<ColumnDefinition> theNotMatchedInFirst,
      List<ColumnDefinition> theNotMatchedInSecond) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, theMatches, theNotMatchedInFirst, theNotMatchedInSecond);
  }
  
  public String getAsHtml() {
    String ret = "<div class=\"col-md-6\">";
    ret += "<div class=\"panel panel-" + getBSClass() + "\">";

    ret += "<div class=\"panel-heading\">Vergleich der definierten Spalten</div>";

    ret += "<div class=\"panel-body\">";
    // Not defined columns
    if(!notMatchedInSecond.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende Spalten wurden nicht definiert: " + notMatchedInSecond.stream()
          .map(ColumnDefinition::getColumnName).collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"))
          + "</div>";
    else
      ret += "<div class=\"alert alert-success\">Es wurden alle nötigen Spalten definiert.</div>";

    // Surplus columns
    if(!notMatchedInFirst.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende überzähligen Spalten wurden definiert: "
          + notMatchedInFirst.stream().map(colDef -> colDef.getColumnName())
              .collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"))
          + "</div>";
    else
      ret += "<div class=\"alert alert-success\">Es wurden keine überschüssigen Spalten definiert.</div>";

    ret += "<hr>";

    // Columns with correct name
    // for(EvaluationResult colRes: matches)
    // ret += colRes.getAsHtml();

    ret += "</div></div></div>";
    return ret;
  }

}
