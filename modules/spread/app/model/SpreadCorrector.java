package model;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class SpreadCorrector<D, S, C, F, ColorType> {

  protected static final String CORRECTION_ADD_STRING = "_Korrektur";
  
  public abstract void closeDocument(D compareDocument);

  public abstract String compareCellFormulas(C masterCell, C compareCell);

  public abstract String compareCellValues(C masterCell, C compareCell);

  public abstract String compareChartsInSheet(S compareSheet, S sampleSheet);

  public abstract String compareNumberOfChartsInDocument(D compareDocument, D sampleDocument);

  public abstract void compareSheet(S sampleTable, S compareTable, boolean conditionalFormating);

  public SpreadSheetCorrectionResult correct(Path samplePath, Path comparePath, boolean conditionalFormating,
      boolean compareCharts) throws CorrectionException {
    
    // Check if both documents exist as files
    if(!samplePath.toFile().exists())
      return new SpreadSheetCorrectionResult(false, Arrays.asList("Musterdatei ist nicht vorhanden!"));
    
    if(!comparePath.toFile().exists())
      return new SpreadSheetCorrectionResult(false, Arrays.asList("Lösungsdatei ist nicht vorhanden!"));
    
    // Load document, if loading returns null, return Error
    D sampleDocument = loadDocument(samplePath);
    if(sampleDocument == null)
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Beim Laden der Musterdatei ist ein Fehler aufgetreten."));

    D compareDocument = loadDocument(comparePath);
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
      S sampleTable = getSheetByIndex(sampleDocument, sheetIndex);
      S compareTable = getSheetByIndex(compareDocument, sheetIndex);
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

  public abstract C getCellByPosition(S table, int row, int column);

  public abstract List<C> getColoredRange(S master);

  public abstract S getSheetByIndex(D sampleDocument, int sheetIndex);

  public abstract int getSheetCount(D sampleDocument);

  /**
   * Loads a document from a given path
   *
   * @param musterPath
   *          - path to the document
   * @return the document if there is a document that can be loaded, else null
   */
  public abstract D loadDocument(Path musterPath) throws CorrectionException;

  public abstract void saveCorrectedSpreadsheet(D compareDocument, Path testPath);

  public abstract void setCellComment(C cell, String comment);

  public abstract void setCellStyle(C cell, F font, ColorType color);

}
