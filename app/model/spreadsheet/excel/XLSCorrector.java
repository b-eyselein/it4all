package model.spreadsheet.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import model.spreadsheet.SpreadCorrector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Color;
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
public class XLSCorrector extends SpreadCorrector<Workbook, Sheet, Cell> {
  
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
  
  private static void setXLSCellComment(Cell cell, String message) {
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
  
  private static void setXLSCellStyle(Cell cell, boolean bool) {
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
  
  @Override
  protected Workbook loadDocument(Path path) {
    try {
      return new XSSFWorkbook(path.toFile());
    } catch (InvalidFormatException | IOException e) {
      // TODO: entsprechenden Fehler werfen!
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  protected int getSheetCount(Workbook document) {
    return document.getNumberOfSheets();
  }
  
  @Override
  protected String compareNumberOfChartsInDocument(Workbook compareDocument, Workbook sampleDocument) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Sheet getSheetByIndex(Workbook document, int sheetIndex) {
    return document.getSheetAt(sheetIndex);
  }
  
  @Override
  protected ArrayList<Cell> getColoredRange(Sheet master) {
    ArrayList<Cell> range = new ArrayList<Cell>();
    for(Row row: master) {
      for(Cell cell: row) {
        Color foreground = cell.getCellStyle().getFillForegroundColorColor();
        Color background = cell.getCellStyle().getFillBackgroundColorColor();
        if(foreground != null && background != null)
          range.add(cell);
      }
    }
    return range;
  }
  
  @Override
  protected void compareSheet(Sheet sampleTable, Sheet compareTable, boolean conditionalFormating) {
    
    // Create SheetComparator
    XLSSheetComparator sc = new XLSSheetComparator(sampleTable, compareTable);
    // Compare conditional formatting
    if(conditionalFormating) {
      sc.compareSheetConditionalFormatting();
    }
    
    if(conditionalFormating)
      setXLSSheetComment(compareTable, sc.getMessage(), 0, 0);
    
    // Iterate over colored cells
    ArrayList<Cell> range = getColoredRange(sampleTable);
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
    
  }
  
  @Override
  protected void saveCorrectedSpreadsheet(Workbook compareDocument, Path testPath) {
    // File dir = new File(userFolder);
    // if(!dir.exists()) {
    // dir.mkdirs();
    // }
    // FileOutputStream fileOut = new FileOutputStream(userFolder + fileName +
    // "_Korrektur." + extension);
    // wb.write(fileOut);
    // fileOut.close();
  }
  
  @Override
  protected void closeDocument(Workbook document) {
    try {
      document.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  protected Cell getCellByPosition(Sheet table, int row, int column) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected void setCellComment(Cell type, String comment) {
    // TODO Auto-generated method stub
    
  }
}
