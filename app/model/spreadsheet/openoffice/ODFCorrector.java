package model.spreadsheet.openoffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import model.spreadsheet.CellStyler;
import model.spreadsheet.SpreadSheetCorrector;
import model.spreadsheet.SheetStyler;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ODFCorrector {
  
  public static String startComparison(String musterPath, String testPath, boolean conditionalFormating, boolean charts) {
    String notice = "";
    try {
      String userFolder = SpreadSheetCorrector.getUserFolder(testPath);
      String fileName = SpreadSheetCorrector.getFileName(testPath);
      // Create workbook of master file
      // SpreadsheetDocument sdMaster =
      // ODFCorrector.getSpreadsheetOfPath(ExcelCorrector.DIR + "muster/" +
      // fileName + "_Muster.ods");
      SpreadsheetDocument sdMaster = ODFCorrector.getSpreadsheetOfPath(musterPath);
      // Create workbook of file to test
      SpreadsheetDocument sdCompare = ODFCorrector.getSpreadsheetOfPath(testPath);
      // Create WorkbookComparison
      ODFWorkbookComparator wc = new ODFWorkbookComparator(sdMaster, sdCompare);
      // Compare sheet number
      if(!wc.compareSheetNum()) {
        return "Sie haben die falsche Datei hochgeladen.";
      }
      if(sdMaster != null) {
        // Iterate over sheets
        int sCount = sdMaster.getSheetCount();
        for(int index = 0; index < sCount; index++) {
          Table tMaster = sdMaster.getSheetByIndex(index);
          Table tCompare = wc.getCompareWorkbook().getSheetByIndex(index);
          // Compare charts
          if(index == 0 && charts) {
            wc.compareSheetCharts();
            SheetStyler.setODFSheetComment(tCompare, wc.getMessage(), 0, 0);
          }
          if(tCompare != null) {
            // Create SheetComparator
            ODFSheetComparator sc = new ODFSheetComparator(tMaster, tCompare);
            // Compare conditional formatting
            if(conditionalFormating) {
              // NOTICE: Does not work in ODF Toolkit
            }
            // Iterate over colored cells
            ArrayList<Cell> range = sc.getColoredRange();
            for(Cell cellMaster: range) {
              int rowIndex = cellMaster.getRowIndex();
              int columnIndex = cellMaster.getColumnIndex();
              Row rowCompare = sc.getSheetCompare().getRowByIndex(rowIndex);
              if(rowCompare != null) {
                Cell cellCompare = rowCompare.getCellByIndex(columnIndex);
                if(cellCompare != null) {
                  // Create CellComparator
                  ODFCellComparator cc = new ODFCellComparator(cellMaster, cellCompare);
                  cellCompare.setNoteText(null);
                  // Compare cell values
                  boolean equalCell = cc.compareCellValues();
                  boolean equalFormula = cc.compareCellFormulas();
                  CellStyler.setODFCellComment(cellCompare, cc.getMessage());
                  if(equalCell && equalFormula) {
                    // Style green
                    CellStyler.setODFCellStyle(cellCompare, true);
                  } else {
                    // Style red
                    CellStyler.setODFCellStyle(cellCompare, false);
                  }
                }
              }
            }
          } else {
            notice = "Ihre Upload-Datei konnte nicht geöffnet werden.";
          }
        }
      } else {
        notice = "Der Test konnte nicht gestartet werden.";
      }
      // Save and close
      ODFCorrector.saveSpreadsheet(wc.getCompareWorkbook(), userFolder, fileName);
      wc.closeWorkbooks();
      if(notice.isEmpty())
        return "Korrektur ist durchgelaufen...";
      return notice;
    } catch (Exception e) {
      e.printStackTrace();
      notice = "Es ist ein unerwarteter Fehler aufgetreten. Bitte wenden Sie sich an den Übungsleiter.";
      return notice;
    }
  }
  
  private static void saveSpreadsheet(SpreadsheetDocument sd, String userFolder, String fileName) throws IOException {
    try {
      File dir = new File(userFolder);
      if(!dir.exists()) {
        dir.mkdirs();
      }
      FileOutputStream fileOut = new FileOutputStream(userFolder + fileName + "_Korrektur.ods");
      sd.save(fileOut);
      fileOut.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  
  private static SpreadsheetDocument getSpreadsheetOfPath(String path) {
    try {
      File file = new File(path);
      FileInputStream fis = new FileInputStream(file);
      SpreadsheetDocument sd = SpreadsheetDocument.loadDocument(fis);
      fis.close();
      return sd;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }
}
