package model.spreadsheet.openoffice;

import model.spreadsheet.StringHelper;

import org.odftoolkit.simple.table.Cell;

public class ODFCellComparator {
  
  private Cell cMaster;
  private Cell cCompare;
  private String message;
  
  public ODFCellComparator(Cell cell1, Cell cell2) {
    this.cMaster = cell1;
    this.cCompare = cell2;
    this.message = "";
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public boolean compareCellValues() {
    String cell1Value = cMaster.getStringValue();
    String cell2Value = cCompare.getStringValue();
    cell2Value = cell2Value.substring(0, cell2Value.indexOf("\n"));
    if(cell2Value.equals("")) {
      this.message += "Wert falsch. Kein Wert eingetragen.\n";
      return false;
    } else if(cell1Value.equals(cell2Value)) {
      this.message += "Wert richtig.\n";
      return true;
    } else {
      this.message += "Wert falsch. Erwartet wurde '" + cell1Value + "'.\n";
      return false;
    }
  }
  
  public boolean compareCellFormulas() {
    String masterFormula = cMaster.getFormula();
    String compareFormula = cCompare.getFormula();
    if(masterFormula == null) {
      // Keine Formel zu vergleichen
      return true;
    } else if(masterFormula.equals(compareFormula)) {
      // Formel richtig
      this.message += "Formel richtig.\n";
      return true;
    } else if(compareFormula == null) {
      // Keine Formel von Student angegeben
      this.message += "Formel falsch. Bitte Formel verwenden.\n";
      return false;
    } else {
      String string = StringHelper.getDiffOfTwoFormulas(masterFormula, compareFormula);
      if(string.equals("")) {
        this.message += "Formel richtig.\n";
        return true;
      } else {
        this.message += "Formel falsch." + string + "\n";
        return false;
      }
    }
  }
  
  @SuppressWarnings("unused")
  private String getStringValueOfCell(Cell cell) {
    String value = cell.getStringValue();
    switch(cell.getValueType()) {
    case "boolean":
      value = cell.getBooleanValue().toString();
      break;
    case "currency":
      value = Double.toString(cell.getCurrencyValue());
      break;
    case "date":
      value = cell.getDateValue().toString();
      break;
    case "float":
      value = Double.toString(cell.getCurrencyValue());
      break;
    case "percentage":
      value = Double.toString(cell.getPercentageValue());
      break;
    case "time":
      value = cell.getTimeValue().toString();
    }
    return value;
  }
  
}
