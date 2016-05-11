package model.spread;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class SpreadCorrector<DocType, SheetType, CellType, FontType, ColorType> {
  
  public abstract void closeDocument(DocType compareDocument);

  public abstract String compareCellFormulas(CellType masterCell, CellType compareCell);

  public abstract String compareCellValues(CellType masterCell, CellType compareCell);

  public abstract String compareChartsInSheet(SheetType compareSheet, SheetType sampleSheet);

  public abstract String compareNumberOfChartsInDocument(DocType compareDocument, DocType sampleDocument);

  public abstract void compareSheet(SheetType sampleTable, SheetType compareTable, boolean conditionalFormating);

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
      SheetType sampleTable = getSheetByIndex(sampleDocument, sheetIndex);
      SheetType compareTable = getSheetByIndex(compareDocument, sheetIndex);
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

  public abstract CellType getCellByPosition(SheetType table, int row, int column);

  public abstract ArrayList<CellType> getColoredRange(SheetType master);

  public abstract SheetType getSheetByIndex(DocType sampleDocument, int sheetIndex);

  public abstract int getSheetCount(DocType sampleDocument);

  public abstract DocType loadDocument(Path musterPath);

  public abstract void saveCorrectedSpreadsheet(DocType compareDocument, Path testPath);

  public abstract void setCellComment(CellType cell, String comment);

  public abstract void setCellStyle(CellType cell, FontType font, ColorType color);

}
