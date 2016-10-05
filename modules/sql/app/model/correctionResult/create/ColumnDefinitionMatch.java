package model.correctionResult.create;

import java.util.List;

import model.exercise.Success;
import model.matching.Match;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class ColumnDefinitionMatch extends Match<ColumnDefinition> {

  private Object datatypeName;

  private Object message;

  public ColumnDefinitionMatch(ColumnDefinition theArg1, ColumnDefinition theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  public void analyze() {
    datatypeName = arg1.getColumnName();

    compareDataTypes(arg1.getColDataType(), arg2.getColDataType());

    // TODO: compare columnspecstrings!
    // Logger.debug("ColumnSpecString for column " + userDef.getColumnName()
    // + "
    // :: " + userDef.getColumnSpecStrings());
  }

  @Override
  public String getAsHtml() {
    String ret = "<div class=\"panel panel-" + getBSClass() + "\">";
    ret += "<div class=\"panel-heading\">Vergleich für Spalte <code>" + datatypeName + "</code></div>";
    ret += "<div class=\"panel-body\">" + message + "</div>";
    ret += "</div>";
    return ret;
  }

  private void compareDataTypes(ColDataType userType, ColDataType sampleType) {
    String userDataType = userType.getDataType().toUpperCase();
    String sampleDataType = sampleType.getDataType().toUpperCase();

    // Comparing datatype
    if(!userDataType.equals(sampleDataType)) {
      message = "Datentyp \"" + userDataType + "\" ist nicht korrekt, erwartet wurde \"" + sampleDataType + "\"!";
      success = Success.NONE;
      return;
    }

    // TODO: Compare argumentslist?
    List<String> userArgs = userType.getArgumentsStringList(), sampleArgs = sampleType.getArgumentsStringList();
    if(userArgs == null && sampleArgs == null) {
      message = "Datentyp richtig spezifiziert";
      success = Success.COMPLETE;
      return;
    }

    // TODO: Use matcher for arguments?
    if(userArgs.size() != sampleArgs.size()) {
      message = "Anzahl der Argumente stimmen nicht überein!";
      success = Success.PARTIALLY;
      return;
    }
    for(int i = 0; i < userArgs.size(); i++) {
      if(!userArgs.get(i).equals(sampleArgs.get(i))) {
        message = "Argument des Datentyps (" + userArgs.get(i) + ") ist nicht korrekt, erwartet wurde: ("
            + sampleArgs.get(i) + ")!";
        success = Success.PARTIALLY;
        return;
      }
    }

    message = "Datentyp richtig spezifiziert";
    success = Success.COMPLETE;
  }

}
