package model.correctionResult.create;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.EvaluationResult;
import model.result.Match;
import model.result.MatchingResult;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class CreateResult extends MatchingResult<ColumnDefinition> {
  
  private List<Match<ColumnDefinition>> columnResults;
  private List<ColumnDefinition> notDefinedColumns;
  private List<ColumnDefinition> surplusColumns;
  
  public CreateResult(List<Match<ColumnDefinition>> theMatches, List<ColumnDefinition> theNotMatchedInFirst,
      List<ColumnDefinition> theNotMatchedInSecond) {
    super(theMatches, theNotMatchedInFirst, theNotMatchedInSecond);
  }
  
  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    
    ret += "<div class=\"panel-heading\">Vergleich der definierten Spalten</div>";
    
    ret += "<div class=\"panel-body\">";
    // Not defined columns
    if(!notDefinedColumns.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende Spalten wurden nicht definiert: " + notDefinedColumns.stream()
          .map(colDef -> colDef.toString()).collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"))
          + "</div>";
    else
      ret += "<div class=\"alert alert-success\">Es wurden alle nötigen Spalten definiert.</div>";
    
    // Surplus columns
    if(!surplusColumns.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende überzähligen Spalten wurden definiert: " + surplusColumns
          .stream().map(colDef -> colDef.toString()).collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>"))
          + "</div>";
    else
      ret += "<div class=\"alert alert-success\">Es wurden keine überschüssigen Spalten definiert.</div>";
    
    ret += "<hr>";
    
    // Columns with correct name
    for(EvaluationResult colRes: columnResults)
      ret += colRes.getAsHtml();
    ret += "</div>";
    
    ret += "</div>";
    return ret;
  }
  
}
