package model;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
  public void closeDocument(SpreadsheetDocument document) {
    document.close();
  }
  
  @Override
  public String compareCellFormulas(Cell masterCell, Cell compareCell) {
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
      if(diffOfTwoFormulas.isEmpty())
        // TODO: can this happen?
        return "Formel richtig.";
      else
        return "Formel falsch. " + diffOfTwoFormulas;
    }
  }
  
  @Override
  public String compareCellValues(Cell masterCell, Cell compareCell) {
    String masterValue = masterCell.getStringValue();
    String compareValue = compareCell.getStringValue();
    // FIXME: why substring from 0 to first newline?
    if(compareValue.indexOf('\n') != -1)
      compareValue = compareValue.substring(0, compareValue.indexOf('\n'));
    if(compareValue.isEmpty())
      return "Keinen Wert angegeben!";
    else if(masterValue.equals(compareValue))
      return "Wert richtig.";
    else
      return "Wert falsch. Erwartet wurde '" + masterValue + "'.";
  }
  
  @Override
  public String compareChartsInSheet(Table compareSheet, Table sampleSheet) {
    // FIXME: nicht von ODFToolkit unterstützt...
    return null;
  }
  
  @Override
  public String compareNumberOfChartsInDocument(SpreadsheetDocument compare, SpreadsheetDocument sample) {
    int sampleCount = sample.getChartCount();
    int compareCount = compare.getChartCount();
    if(sampleCount == 0)
      return "Es waren keine Diagramme zu erstellen.";
    else if(sampleCount != compareCount)
      return "Falsche Anzahl Diagramme im Dokument (erwartet: " + sampleCount + ", gezählt: " + compareCount + ").";
    else
      return "Richtige Anzahl Diagramme gefunden.";
  }
  
  @Override
  public void compareSheet(Table sampleTable, Table compareTable, boolean correctConditionalFormating) {
    if(correctConditionalFormating) {
      // NOTICE: Does not work in ODF Toolkit
    }
    // Iterate over colored cells
    List<Cell> range = getColoredRange(sampleTable);
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
      
      // FIXME: use enum instead of String!
      if("Wert richtig.".equals(cellValueResult)
          && (cellFormulaResult.isEmpty() || "Formel richtig.".equals(cellFormulaResult)))
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
  public Cell getCellByPosition(Table table, int column, int row) {
    return table.getCellByPosition(column, row);
  }
  
  @Override
  @SuppressWarnings("deprecation")
  public List<Cell> getColoredRange(Table master) {
    List<Cell> range = new ArrayList<>();
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
  public Table getSheetByIndex(SpreadsheetDocument document, int sheetIndex) {
    return document.getSheetByIndex(sheetIndex);
  }
  
  @Override
  public int getSheetCount(SpreadsheetDocument document) {
    return document.getSheetCount();
  }
  
  @Override
  public SpreadsheetDocument loadDocument(Path path) {
    try {
      return SpreadsheetDocument.loadDocument(path.toFile());
    } catch (Exception e) {
      return null;
    }
  }
  
  @Override
  public void saveCorrectedSpreadsheet(SpreadsheetDocument document, Path testPath) {
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
  public void setCellComment(Cell cell, String message) {
    if(message == null || message.isEmpty())
      return;
    cell.setNoteText(message);
  }
  
  @Override
  public void setCellStyle(Cell cell, Font font, Color color) {
    font.setColor(color);
    cell.setFont(font);
  }
  
}