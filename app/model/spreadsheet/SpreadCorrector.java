package model.spreadsheet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class SpreadCorrector<DocType, TableType, CellType, FontType, ColorType> {
  
  public SpreadSheetCorrectionResult correct(Path musterPath, Path testPath, boolean conditionalFormating,
      boolean compareCharts) {
    LinkedList<String> notices = new LinkedList<String>();
    
    DocType sampleDocument = null;
    DocType compareDocument = null;
    try {
      sampleDocument = loadDocument(musterPath);
      compareDocument = loadDocument(testPath);
    } catch (Exception e) {
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Test konnte nicht gestartet werden. Beim Laden der Dateien ist ein Fehler aufgetreten."));
    }
    
    if(sampleDocument == null || compareDocument == null)
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Test konnte nicht gestartet werden. Beim Laden der Dateien ist ein Fehler aufgetreten."));
    
    if(getSheetCount(sampleDocument) != getSheetCount(compareDocument))
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Anzahl an Arbeitsblättern stimmt nicht überein. Haben Sie die richtige Datei hochgeladen?"));
    
    if(compareCharts) {
      String message = compareNumberOfChartsInDocument(compareDocument, sampleDocument);
      // write message in Cell A0 on first Sheet
      setCellComment(getCellByPosition(getSheetByIndex(compareDocument, 0), 0, 0), message);
    }
    
    // Iterate over sheets
    int sheetCount = getSheetCount(sampleDocument);
    for(int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
      TableType sampleTable = getSheetByIndex(sampleDocument, sheetIndex);
      TableType compareTable = getSheetByIndex(compareDocument, sheetIndex);
      if(compareTable == null || sampleTable == null)
        notices.add("Es gab einen Fehler beim Öffnen der " + (sheetCount + 1) + ". Tabelle!");
      else
        compareSheet(sampleTable, compareTable, conditionalFormating);
    }
    
    // Save and close workbooks
    saveCorrectedSpreadsheet(compareDocument, testPath);
    closeDocument(compareDocument);
    closeDocument(sampleDocument);
    
    if(notices.isEmpty())
      return new SpreadSheetCorrectionResult(true, Arrays.asList("Korrektur ist erfolgreicht durchgelaufen."));
    else
      return new SpreadSheetCorrectionResult(false, notices);
  }
  
  protected abstract String compareCellValues(CellType masterCell, CellType compareCell);
  
  protected abstract String compareCellFormulas(CellType masterCell, CellType compareCell);
  
  protected abstract DocType loadDocument(Path musterPath);
  
  protected abstract int getSheetCount(DocType sampleDocument);
  
  protected abstract String compareNumberOfChartsInDocument(DocType compareDocument, DocType sampleDocument);
  
  protected abstract CellType getCellByPosition(TableType table, int row, int column);
  
  protected abstract TableType getSheetByIndex(DocType sampleDocument, int sheetIndex);
  
  protected abstract ArrayList<CellType> getColoredRange(TableType master);
  
  protected abstract void compareSheet(TableType sampleTable, TableType compareTable, boolean conditionalFormating);
  
  protected abstract void saveCorrectedSpreadsheet(DocType compareDocument, Path testPath);
  
  protected abstract void closeDocument(DocType compareDocument);
  
  protected abstract void setCellComment(CellType cell, String comment);
  
  protected abstract void setCellStyle(CellType cell, FontType font, ColorType color);
  
}
