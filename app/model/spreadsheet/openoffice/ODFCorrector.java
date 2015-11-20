package model.spreadsheet.openoffice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import model.spreadsheet.SpreadSheetCorrectionResult;
import model.spreadsheet.SpreadSheetCorrector;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class ODFCorrector {
  
  // TODO: magic numbers...
  private static final int MAXROW = 80;
  private static final int MAXCOLUMN = 22;
  
  public static SpreadSheetCorrectionResult correct(Path musterPath, Path testPath, boolean conditionalFormating,
      boolean compareCharts) {
    LinkedList<String> notices = new LinkedList<String>();
    
    SpreadsheetDocument sampleDocument = null;
    SpreadsheetDocument compareDocument = null;
    try {
      sampleDocument = SpreadsheetDocument.loadDocument(musterPath.toFile());
      compareDocument = SpreadsheetDocument.loadDocument(testPath.toFile());
    } catch (Exception e) {
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Test konnte nicht gestartet werden. Beim Laden der Dateien ist ein Fehler aufgetreten."));
    }
    
    if(sampleDocument == null || compareDocument == null)
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Test konnte nicht gestartet werden. Beim Laden der Dateien ist ein Fehler aufgetreten."));
    
    if(sampleDocument.getSheetCount() != compareDocument.getSheetCount())
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Anzahl an Arbeitsblättern stimmt nicht überein. Haben Sie die richtige Datei hochgeladen?"));
    
    if(compareCharts)
      compareNumberOfChartsInSheet(compareDocument, sampleDocument);
    
    // Iterate over sheets
    int sheetCount = sampleDocument.getSheetCount();
    for(int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
      Table sampleTable = sampleDocument.getSheetByIndex(sheetIndex);
      Table compareTable = compareDocument.getSheetByIndex(sheetIndex);
      if(compareTable == null || sampleTable == null)
        notices.add("Es gab einen Fehler beim Öffnen der " + (sheetCount + 1) + ". Tabelle!");
      else
        compareSheet(sampleTable, compareTable, conditionalFormating);
    }
    
    // Save and close
    // TODO userFolder: saveFolder!
    String userFolder = SpreadSheetCorrector.getUserFolder(testPath);
    String fileName = SpreadSheetCorrector.getFileName(testPath);
    try {
      ODFCorrector.saveSpreadsheet(compareDocument, userFolder, fileName);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // Close Workbooks
    compareDocument.close();
    sampleDocument.close();
    
    if(notices.isEmpty())
      return new SpreadSheetCorrectionResult(true, Arrays.asList("Korrektur ist erfolgreicht durchgelaufen."));
    else
      return new SpreadSheetCorrectionResult(false, notices);
  }
  
  private static void compareNumberOfChartsInSheet(SpreadsheetDocument compare, SpreadsheetDocument sample) {
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
  
  private static void compareSheet(Table sampleTable, Table compareTable, boolean correctConditionalFormating) {
    if(correctConditionalFormating) {
      // NOTICE: Does not work in ODF Toolkit
    }
    // Iterate over colored cells
    ArrayList<Cell> range = getColoredRange(sampleTable);
    for(Cell cellMaster: range) {
      int rowIndex = cellMaster.getRowIndex();
      int columnIndex = cellMaster.getColumnIndex();
      Cell cellCompare = compareTable.getCellByPosition(columnIndex, rowIndex);
      
      if(cellCompare != null) {
        // Create CellComparator
        ODFCellComparator cc = new ODFCellComparator(cellMaster, cellCompare);
        cellCompare.setNoteText(null);
        // Compare cell values
        boolean equalCell = cc.compareCellValues();
        boolean equalFormula = cc.compareCellFormulas();
        setODFCellComment(cellCompare, cc.getMessage());
        
        if(equalCell && equalFormula)
          cellCompare.setFont(new Font("Arial", FontStyle.BOLD, 10, Color.GREEN));
        else
          cellCompare.setFont(new Font("Arial", FontStyle.ITALIC, 10, Color.RED));
      }
    }
  }
  
  private static void setODFCellComment(Cell cell, String message) {
    if(message.isEmpty())
      return;
    // TODO: Why remove note if exists
    if(cell.getNoteText() != null)
      cell.setNoteText(null);
    cell.setNoteText(message);
  }
  
  @SuppressWarnings("deprecation")
  private static ArrayList<Cell> getColoredRange(Table master) {
    ArrayList<Cell> range = new ArrayList<Cell>();
    for(int row = 0; row < MAXROW; row++) {
      for(int column = 0; column < MAXCOLUMN; column++) {
        Cell oCell = master.getRowByIndex(row).getCellByIndex(column);
        if(!oCell.getCellBackgroundColorString().equals("#FFFFFF"))
          range.add(oCell);
      }
    }
    return range;
  }
  
  private static void saveSpreadsheet(SpreadsheetDocument document, String userFolder, String fileName) throws IOException {
    try {
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
  
}
