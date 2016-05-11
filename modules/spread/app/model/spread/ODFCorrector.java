package model.spread;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class ODFCorrector extends SpreadCorrector<SpreadsheetDocument, Table, Cell, Font, Color> {
  
  // TODO: magic numbers...
  private static final int MAXROW = 80;
  private static final int MAXCOLUMN = 22;
  
  private static final String COLOR_WHITE = "#FFFFFF";
  private static final String FONT = "Arial";
  private static final double FONT_SIZE = 10.;
  
  @Override
  protected void closeDocument(SpreadsheetDocument document) {
    document.close();
  }
  
  @Override
  protected String compareCellFormulas(Cell masterCell, Cell compareCell) {
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
      String diffOfTwoFormulas = HashSetHelper.getDiffOfTwoFormulas(masterFormula, compareFormula);
      if(diffOfTwoFormulas.equals(""))
        // TODO: can this happen?
        return "Formel richtig.";
      else
        return "Formel falsch. " + diffOfTwoFormulas;
    }
  }
  
  @Override
  protected String compareCellValues(Cell masterCell, Cell compareCell) {
    String masterValue = masterCell.getStringValue(), compareValue = compareCell.getStringValue();
    // FIXME: why substring from 0 to first newline?
    if(compareValue.indexOf("\n") != -1)
      compareValue = compareValue.substring(0, compareValue.indexOf("\n"));
    if(compareValue.isEmpty())
      return "Keinen Wert angegeben!";
    else if(masterValue.equals(compareValue))
      return "Wert richtig.";
    else
      return "Wert falsch. Erwartet wurde '" + masterValue + "'.";
  }
  
  @Override
  protected String compareChartsInSheet(Table compareSheet, Table sampleSheet) {
    // FIXME: nicht von ODFToolkit unterstützt...
    return null;
  }
  
  @Override
  protected String compareNumberOfChartsInDocument(SpreadsheetDocument compare, SpreadsheetDocument sample) {
    int sampleCount = sample.getChartCount(), compareCount = compare.getChartCount();
    if(sampleCount == 0)
      return "Es waren keine Diagramme zu erstellen.";
    else if(sampleCount != compareCount)
      return "Falsche Anzahl Diagramme im Dokument (erwartet: " + sampleCount + ", gezählt: " + compareCount + ").";
    else
      return "Richtige Anzahl Diagramme gefunden.";
  }
  
  @Override
  protected void compareSheet(Table sampleTable, Table compareTable, boolean correctConditionalFormating) {
    if(correctConditionalFormating) {
      // NOTICE: Does not work in ODF Toolkit
    }
    // Iterate over colored cells
    ArrayList<Cell> range = getColoredRange(sampleTable);
    for(Cell cellMaster: range) {
      int rowIndex = cellMaster.getRowIndex();
      int columnIndex = cellMaster.getColumnIndex();
      Cell cellCompare = compareTable.getCellByPosition(columnIndex, rowIndex);
      
      if(cellCompare == null)
        // TODO: Fehler werfen? Kann das überhaupt passieren?
        return;
      
      // Compare cell values
      String cellValueResult = compareCellValues(cellMaster, cellCompare);
      String cellFormulaResult = compareCellFormulas(cellMaster, cellCompare);
      
      setCellComment(cellCompare, cellValueResult + "\n" + cellFormulaResult);
      
      if(cellValueResult.equals("Wert richtig.")
          && (cellFormulaResult.isEmpty() || cellFormulaResult.equals("Formel richtig.")))
        setCellStyle(cellCompare, new Font(FONT, FontStyle.BOLD, FONT_SIZE), Color.GREEN);
      // cellCompare.setFont(new Font(FONT, FontStyle.BOLD, FONT_SIZE,
      // Color.GREEN));
      else
        setCellStyle(cellCompare, new Font(FONT, FontStyle.ITALIC, FONT_SIZE), Color.RED);
      // cellCompare.setFont(new Font(FONT, FontStyle.ITALIC, FONT_SIZE,
      // Color.RED));
    }
  }
  
  @Override
  protected Cell getCellByPosition(Table table, int column, int row) {
    return table.getCellByPosition(column, row);
  }
  
  @Override
  @SuppressWarnings("deprecation")
  protected ArrayList<Cell> getColoredRange(Table master) {
    ArrayList<Cell> range = new ArrayList<Cell>();
    for(int row = 0; row < MAXROW; row++) {
      for(int column = 0; column < MAXCOLUMN; column++) {
        Cell oCell = master.getRowByIndex(row).getCellByIndex(column);
        if(!oCell.getCellBackgroundColorString().equals(COLOR_WHITE))
          range.add(oCell);
      }
    }
    return range;
  }
  
  @Override
  protected Table getSheetByIndex(SpreadsheetDocument document, int sheetIndex) {
    return document.getSheetByIndex(sheetIndex);
  }
  
  @Override
  protected int getSheetCount(SpreadsheetDocument document) {
    return document.getSheetCount();
  }
  
  @Override
  protected SpreadsheetDocument loadDocument(Path path) {
    try {
      return SpreadsheetDocument.loadDocument(path.toFile());
    } catch (Exception e) {
      return null;
    }
  }
  
  @Override
  protected void saveCorrectedSpreadsheet(SpreadsheetDocument document, Path testPath) {
    // TODO userFolder: saveFolder!
    String userFolder = SpreadSheetCorrector.getUserFolder(testPath);
    String fileName = SpreadSheetCorrector.getFileName(testPath);
    try {
      // FIXME: use document.save(File file);
      // File saveTo = new File("TODO");
      File dir = new File(userFolder);
      if(!dir.exists()) {
        dir.mkdirs();
      }
      FileOutputStream fileOut = new FileOutputStream(userFolder + fileName + "_Korrektur.ods");
      document.save(fileOut);
      fileOut.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  
  @Override
  protected void setCellComment(Cell cell, String message) {
    if(message == null || message.isEmpty())
      return;
    cell.setNoteText(message);
  }
  
  @Override
  protected void setCellStyle(Cell cell, Font font, Color color) {
    font.setColor(color);
    cell.setFont(font);
  }
  
}
