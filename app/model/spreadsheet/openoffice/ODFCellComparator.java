package model.spreadsheet.openoffice;

import model.spreadsheet.StringHelper;

import org.odftoolkit.simple.table.Cell;

public class ODFCellComparator {
  
  public static String compareCellValues(Cell masterCell, Cell compareCell) {
    String masterValue = masterCell.getStringValue(), compareValue = compareCell.getStringValue();
    // FIXME: why substring from 0 to first newline?
    compareValue = compareValue.substring(0, compareValue.indexOf("\n"));
    if(compareValue.isEmpty())
      return "Keinen Wert angegeben!";
    else if(masterValue.equals(compareValue))
      return "Wert richtig.";
    else
      return "Wert falsch. Erwartet wurde '" + masterValue + "'.";
  }
  
  public static String compareCellFormulas(Cell masterCell, Cell compareCell) {
    String masterFormula = masterCell.getFormula();
    String compareFormula = compareCell.getFormula();
    if(masterFormula == null)
      // Keine Formel zu vergleichen
      return "";
    else if(masterFormula.equals(compareFormula))
      // Formel richtig
      return "Formel richtig.";
    else if(compareFormula == null)
      // Keine Formel von Student angegeben
      return "Keine Formel angegeben!";
    else {
      String string = StringHelper.getDiffOfTwoFormulas(masterFormula, compareFormula);
      if(string.equals(""))
        // TODO: can this happen?
        return "Formel richtig.";
      else
        return "Formel falsch. " + string;
    }
  }
}
