package model.spread;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class SpreadCorrector<DocType, SheetType, CellType, FontType, ColorType> {
  
  protected static final String CORRECTION_ADD_STRING = "_Korrektur";

  public abstract void closeDocument(DocType compareDocument);
  
  public abstract String compareCellFormulas(CellType masterCell, CellType compareCell);
  
  public abstract String compareCellValues(CellType masterCell, CellType compareCell);
  
  public abstract String compareChartsInSheet(SheetType compareSheet, SheetType sampleSheet);
  
  public abstract String compareNumberOfChartsInDocument(DocType compareDocument, DocType sampleDocument);
  
  public abstract void compareSheet(SheetType sampleTable, SheetType compareTable, boolean conditionalFormating);
  
  public SpreadSheetCorrectionResult correct(Path samplePath, Path comparePath, boolean conditionalFormating,
      boolean compareCharts) {

    // Check if both documents exist as files
    if(!Files.exists(samplePath))
      return new SpreadSheetCorrectionResult(false, Arrays.asList("Musterdatei ist nicht vorhanden!"));
    if(!Files.exists(comparePath))
      return new SpreadSheetCorrectionResult(false, Arrays.asList("Lösungsdatei ist nicht vorhanden!"));

    // Load document, if loading returns null, return Error
    DocType sampleDocument = loadDocument(samplePath);
    DocType compareDocument = loadDocument(comparePath);
    if(sampleDocument == null)
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Beim Laden der Musterdatei ist ein Fehler aufgetreten."));
    if(compareDocument == null)
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Beim Laden der eingereichten Datei ist ein Fehler aufgetreten."));

    if(getSheetCount(sampleDocument) != getSheetCount(compareDocument))
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Anzahl an Arbeitsblättern stimmt nicht überein. Haben Sie die richtige Datei hochgeladen?"));

    if(compareCharts) {
      String message = compareNumberOfChartsInDocument(compareDocument, sampleDocument);
      // write message in Cell A0 on first Sheet
      setCellComment(getCellByPosition(getSheetByIndex(compareDocument, 0), 0, 0), message);
    }
    
    LinkedList<String> notices = new LinkedList<>();
    
    // Iterate over sheets
    int sheetCount = getSheetCount(sampleDocument);
    for(int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
      SheetType sampleTable = getSheetByIndex(sampleDocument, sheetIndex);
      SheetType compareTable = getSheetByIndex(compareDocument, sheetIndex);
      if(compareTable == null || sampleTable == null)
        notices.add("Es gab einen Fehler beim Öffnen der " + (sheetCount + 1) + ". Tabelle!");
      else
        compareSheet(sampleTable, compareTable, conditionalFormating);
    }
    
    // Save and close workbooks
    saveCorrectedSpreadsheet(compareDocument, comparePath);
    closeDocument(compareDocument);
    closeDocument(sampleDocument);
    
    if(notices.isEmpty())
      return new SpreadSheetCorrectionResult(true, Arrays.asList("Korrektur ist erfolgreicht durchgelaufen."));
    else
      return new SpreadSheetCorrectionResult(false, notices);
  }
  
  public abstract CellType getCellByPosition(SheetType table, int row, int column);
  
  public abstract ArrayList<CellType> getColoredRange(SheetType master);
  
  public abstract SheetType getSheetByIndex(DocType sampleDocument, int sheetIndex);
  
  public abstract int getSheetCount(DocType sampleDocument);
  
  /**
   * Loads a document from a given path
   *
   * @param musterPath
   *          - path to the document
   * @return the document if there is a document that can be loaded, else null
   */
  public abstract DocType loadDocument(Path musterPath);
  
  public abstract void saveCorrectedSpreadsheet(DocType compareDocument, Path testPath);
  
  public abstract void setCellComment(CellType cell, String comment);
  
  public abstract void setCellStyle(CellType cell, FontType font, ColorType color);
  
}
