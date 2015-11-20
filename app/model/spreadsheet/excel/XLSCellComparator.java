package model.spreadsheet.excel;

import model.spreadsheet.StringHelper;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class XLSCellComparator {
  
  private Cell cMaster;
  private Cell cCompare;
  private String message;
  
  public XLSCellComparator(Cell cell1, Cell cell2) {
    this.cMaster = cell1;
    this.cCompare = cell2;
    this.message = "";
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public boolean compareCellValues() {
    Cell cell1 = this.cMaster;
    Cell cell2 = this.cCompare;
    String cell1Value = this.getStringValueOfCell(cell1);
    String cell2Value = this.getStringValueOfCell(cell2);
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
    Cell cell1 = this.cMaster;
    Cell cell2 = this.cCompare;
    if(cell1.getCellType() == Cell.CELL_TYPE_FORMULA) {
      if(cell1.toString().equals(cell2.toString())) {
        this.message += "Formel richtig.\n";
        return true;
      } else {
        if(cell2.getCellType() != Cell.CELL_TYPE_FORMULA) {
          this.message += "Formel falsch. Bitte Formel verwenden.\n";
        } else {
          String string = StringHelper.getDiffOfTwoFormulas(cell1.toString(), cell2.toString());
          if(string.equals("")) {
            this.message += "Formel richtig.\n";
            return true;
          } else {
            this.message += "Formel falsch." + string + "\n";
            return false;
          }
        }
        return false;
      }
    }
    return true;
  }
  
  private String getStringValueOfCell(Cell cell) {
    String value = cell.toString();
    if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
      switch(cell.getCachedFormulaResultType()) {
      case Cell.CELL_TYPE_NUMERIC:
        value = Double.toString(cell.getNumericCellValue());
        break;
      case Cell.CELL_TYPE_STRING:
        value = cell.getRichStringCellValue().toString();
        break;
      }
    }
    return value;
  }
  
}
