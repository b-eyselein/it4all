package model.correctionResult.create;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.EvaluationResult;

public class CreateResult extends EvaluationResult {
  
  private List<ColumnDefinitionResult> columnResults;
  private List<String> notDefinedColumns;
  private List<String> surplusColumns;

  public CreateResult(List<ColumnDefinitionResult> theColumnResults, List<String> theNotDefinedColumns,
      List<String> theWrongColumns) {
    columnResults = theColumnResults;
    notDefinedColumns = theNotDefinedColumns;
    surplusColumns = theWrongColumns;
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    
    ret += "<div class=\"panel-heading\">Vergleich der definierten Spalten</div>";
    
    ret += "<div class=\"panel-body\">";
    // Not defined columns
    if(!notDefinedColumns.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende Spalten wurden nicht definiert: "
          + notDefinedColumns.stream().collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>")) + "</div>";
    else
      ret += "<div class=\"alert alert-success\">Es wurden alle nötigen Spalten definiert.</div>";
    
    // Surplus columns
    if(!surplusColumns.isEmpty())
      ret += "<div class=\"alert alert-danger\">Folgende überzähligen Spalten wurden definiert: "
          + surplusColumns.stream().collect(Collectors.joining("</li><li>", "<ul><li>", "</li></ul>")) + "</div>";
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
