package model.spreadsheet.openoffice;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;

import model.spreadsheet.SpreadCorrector;
import model.spreadsheet.SpreadSheetCorrector;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class ODFCorrector extends SpreadCorrector<SpreadsheetDocument, Table, Cell> {
  
  // TODO: magic numbers...
  private static final int MAXROW = 80;
  private static final int MAXCOLUMN = 22;
  
  private static final String COLOR_WHITE = "#FFFFFF";
  private static final String FONT = "Arial";
  private static final double FONT_SIZE = 10.;
  
  private void setODFCellComment(Cell cell, String message) {
    if(message.isEmpty())
      return;
    // TODO: Warum auf null setzen, wenn sowiese überschrieben?
    if(cell.getNoteText() != null)
      cell.setNoteText(null);
    cell.setNoteText(message);
  }
  
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
  protected SpreadsheetDocument loadDocument(Path path) {
    try {
      return SpreadsheetDocument.loadDocument(path.toFile());
    } catch (Exception e) {
      return null;
    }
  }
  
  @Override
  protected int getSheetCount(SpreadsheetDocument document) {
    return document.getSheetCount();
  }
  
  protected void compareNumberOfChartsInDocument(SpreadsheetDocument compare, SpreadsheetDocument sample) {
    int sampleCount = sample.getChartCount(), compareCount = compare.getChartCount();
    String message = "";
    
    if(sampleCount == 0)
      message = "Es waren keine Diagramme zu erstellen.";
    else if(sampleCount != compareCount)
      message = "Falsche Anzahl Diagramme im Dokument (erwartet: " + sampleCount + ", gezählt: " + compareCount + ").";
    else
      message = "Richtige Anzahl Diagramme gefunden.";
    
    // write message in Cell A0 on first Sheet
    setODFCellComment(compare.getSheetByIndex(0).getCellByPosition(0, 0), message);
  }
  
  @Override
  protected Table getSheetByIndex(SpreadsheetDocument document, int sheetIndex) {
    return document.getSheetByIndex(sheetIndex);
  }
  
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
      String cellValueResult = ODFCellComparator.compareCellValues(cellMaster, cellCompare);
      String cellFormulaResult = ODFCellComparator.compareCellFormulas(cellMaster, cellCompare);
      
      setODFCellComment(cellCompare, cellValueResult + "\n" + cellFormulaResult);
      
      if(cellValueResult.equals("Wert richtig.")
          && (cellFormulaResult.isEmpty() || cellFormulaResult.equals("Formel richtig.")))
        cellCompare.setFont(new Font(FONT, FontStyle.BOLD, FONT_SIZE, Color.GREEN));
      else
        cellCompare.setFont(new Font(FONT, FontStyle.ITALIC, FONT_SIZE, Color.RED));
    }
  }
  
  protected void saveCorrectedSpreadsheet(SpreadsheetDocument document, Path testPath) {
    // TODO userFolder: saveFolder!
    String userFolder = SpreadSheetCorrector.getUserFolder(testPath);
    String fileName = SpreadSheetCorrector.getFileName(testPath);
    try {
      // FIXME: use document.save(File file);
      File saveTo = new File("TODO");
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
  protected void closeDocument(SpreadsheetDocument document) {
    try {
      // FIXME: try/catch entfernen!
      document.close();
    } catch (Exception e) {
      System.out.println(e.getStackTrace());
    }
  }
  
}
