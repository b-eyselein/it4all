package model.spreadsheet.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import model.spreadsheet.SpreadSheetCorrector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class XLSCorrector {
  
  public static String correct(Path musterPath, Path testPath, boolean conditionalFormating, boolean charts) {
    String notice = "";
    try {
      String fileName = SpreadSheetCorrector.getFileName(testPath);
      String extension = SpreadSheetCorrector.getExtension(testPath);
      // Create workbook of master file
      // Workbook wbMaster = XLSCorrector.getWorkbookOfPath(ExcelCorrector.DIR +
      // "muster/" + fileName + "_Muster." + extension);
      Workbook wbMaster = XLSCorrector.getWorkbookOfPath(musterPath);
      // Create workbook of file to test
      Workbook wbCompare = XLSCorrector.getWorkbookOfPath(testPath);
      // Create WorkbookComparison
      XLSWorkbookComparator wc = new XLSWorkbookComparator(wbMaster, wbCompare);
      // Compare sheet number
      if(!wc.compareSheetNum()) {
        return "Sie haben die falsche Datei hochgeladen.";
      }
      if(wbMaster != null) {
        // Iterate over sheets
        for(Sheet shMaster: wc.getMasterWorkbook()) {
          int index = shMaster.getWorkbook().getSheetIndex(shMaster);
          Sheet shCompare = wc.getCompareWorkbook().getSheetAt(index);
          if(shCompare != null) {
            // Create SheetComparator
            XLSSheetComparator sc = new XLSSheetComparator(shMaster, shCompare);
            // Compare conditional formatting
            if(conditionalFormating) {
              sc.compareSheetConditionalFormatting();
            }
            // Compare charts
            if(charts) {
              sc.compareSheetCharts();
            }
            if(conditionalFormating || charts)
              setXLSSheetComment(shCompare, sc.getMessage(), 0, 0);
            
            // Iterate over colored cells
            ArrayList<Cell> range = sc.getColoredRange();
            for(Cell cellMaster: range) {
              int rowIndex = cellMaster.getRowIndex();
              int columnIndex = cellMaster.getColumnIndex();
              Row rowCompare = sc.getSheetCompare().getRow(rowIndex);
              if(rowCompare != null) {
                Cell cellCompare = rowCompare.getCell(columnIndex);
                if(cellCompare != null) {
                  // Create CellComparator
                  XLSCellComparator cc = new XLSCellComparator(cellMaster, cellCompare);
                  cellCompare.setCellComment(null);
                  // Compare cell values
                  boolean equalCell = cc.compareCellValues();
                  boolean equalFormula = cc.compareCellFormulas();
                  setXLSCellComment(cellCompare, cc.getMessage());
                  if(equalCell && equalFormula) {
                    // Style green
                    setXLSCellStyle(cellCompare, true);
                  } else {
                    // Style red
                    setXLSCellStyle(cellCompare, false);
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
      // TODO userFolder: saveFolder!
      String userFolder = SpreadSheetCorrector.getUserFolder(testPath);
      XLSCorrector.saveWorkbook(wc.getCompareWorkbook(), userFolder, fileName, extension);
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
  
  private static void saveWorkbook(Workbook wb, String userFolder, String fileName, String extension) throws Exception {
    File dir = new File(userFolder);
    if(!dir.exists()) {
      dir.mkdirs();
    }
    FileOutputStream fileOut = new FileOutputStream(userFolder + fileName + "_Korrektur." + extension);
    wb.write(fileOut);
    fileOut.close();
  }
  
  private static Workbook getWorkbookOfPath(Path path) {
    // TODO: Path is >> always << of extension ".xlsx" or ".xlsm" --> SpreadSheetCorrector!
    try {
      return new XSSFWorkbook(path.toFile());
    } catch (InvalidFormatException | IOException e) {
      // TODO: entsprechenden Fehler werfen!
      e.printStackTrace();
      return null;
    }
  }
  
  private static void setXLSSheetComment(Sheet sheet, String message, int row, int column) {
    if(!message.equals("")) {
      org.apache.poi.ss.usermodel.Cell cell = sheet.getRow(row).getCell(column);
      // Remove comment if exists
      if(cell.getCellComment() != null) {
        cell.removeCellComment();
      }
      // Remove comment if exists
      if(cell.getCellComment() != null) {
        cell.removeCellComment();
      }
      // Create new drawing object
      Drawing drawing = sheet.createDrawingPatriarch();
      CreationHelper factory = sheet.getWorkbook().getCreationHelper();
      // Create comment space
      ClientAnchor anchor = factory.createClientAnchor();
      anchor.setCol1(cell.getColumnIndex() + 1);
      anchor.setCol2(cell.getColumnIndex() + 3);
      anchor.setRow1(cell.getRowIndex() + 1);
      anchor.setRow2(cell.getRowIndex() + 3);
      // Insert new comment
      Comment comment = drawing.createCellComment(anchor);
      RichTextString str = factory.createRichTextString(message);
      comment.setVisible(true);
      comment.setString(str);
      // Set comment
      cell.setCellComment(comment);
    }
  }
  
  private static void setXLSCellComment(org.apache.poi.ss.usermodel.Cell cell, String message) {
    if(!message.equals("")) {
      // Remove comment if exists
      if(cell.getCellComment() != null) {
        cell.removeCellComment();
      }
      // Create new drawing object
      Drawing drawing = cell.getSheet().createDrawingPatriarch();
      CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
      // Create comment space
      ClientAnchor anchor = factory.createClientAnchor();
      anchor.setCol1(cell.getColumnIndex());
      anchor.setCol2(cell.getColumnIndex() + 3);
      anchor.setRow1(cell.getRowIndex());
      anchor.setRow2(cell.getRowIndex() + 3);
      // Insert new comment
      Comment comment = drawing.createCellComment(anchor);
      RichTextString str = factory.createRichTextString(message);
      comment.setVisible(false);
      comment.setString(str);
      // Set comment
      cell.setCellComment(comment);
    }
  }
  
  private static void setXLSCellStyle(org.apache.poi.ss.usermodel.Cell cell, boolean bool) {
    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
    short alignment = cell.getCellStyle().getAlignment();
    short format = cell.getCellStyle().getDataFormat();
    Font font = cell.getSheet().getWorkbook().createFont();
    if(bool) {
      font.setBold(true);
      font.setColor(IndexedColors.GREEN.getIndex());
    } else {
      font.setItalic(true);
      font.setColor(IndexedColors.RED.getIndex());
    }
    style.setFont(font);
    style.setAlignment(alignment);
    style.setDataFormat(format);
    style.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
    cell.setCellStyle(style);
  }
  
}
