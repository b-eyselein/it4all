package model.spreadsheet.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import model.spreadsheet.RegExpHelper;
import model.spreadsheet.SpreadCorrector;
import model.spreadsheet.StringHelper;

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
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class XLSCorrector extends SpreadCorrector<Workbook, Sheet, Cell> {
  
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
    } catch (Exception e) {
      // e.printStackTrace();
      // TODO: IllegalStateException: Falscher Path!
      return null;
    }
  }
  
  @Override
  protected int getSheetCount(Workbook document) {
    return document.getNumberOfSheets();
  }
  
  @Override
  protected String compareNumberOfChartsInDocument(Workbook compareDocument, Workbook sampleDocument) {
    // FIXME: compare Charts by sheet!
    int sheetNumber = 0;
    
    XSSFDrawing drawing1 = ((XSSFSheet) sampleDocument.getSheetAt(sheetNumber)).createDrawingPatriarch();
    XSSFDrawing drawing2 = ((XSSFSheet) compareDocument.getSheetAt(sheetNumber)).createDrawingPatriarch();
    int count1 = drawing1.getCharts().size();
    int count2 = drawing2.getCharts().size();
    if(count1 == 0)
      return "Keine Diagramme zu erstellen.";
    if(count2 == 0) {
      return "Diagramm falsch. Kein Diagramm gefunden.";
    } else if(count1 != count2) {
      return "Diagramm falsch. Zu wenig Diagramme (Erwartet: " + count1 + ").";
    } else {
      String message = "";
      for(int i = 0; i < count1; i++) {
        CTChart chartMaster = drawing1.getCharts().get(i).getCTChart();
        CTChart chartCompare = drawing2.getCharts().get(i).getCTChart();
        if(chartCompare == null)
          message += "Sheet konnte nicht geöffnet werden!";
        else {
          message += "Diagramm falsch.";
          String stringMaster = chartMaster.toString();
          String stringCompare = chartCompare.toString();
          // Compare Title
          String title1 = RegExpHelper.getExcelChartTitle(stringMaster);
          String title2 = RegExpHelper.getExcelChartTitle(stringCompare);
          if(!title1.equals(title2)) {
            message += " Der Titel sollte " + title1 + " lauten.";
          } else {
            // Compare ranges
            String chDiff = RegExpHelper.getExcelChartRangesDiff(sampleDocument.getSheetAt(sheetNumber).getSheetName(),
                stringMaster, compareDocument.getSheetAt(sheetNumber).getSheetName(), stringCompare);
            if(chDiff != "") {
              message += " Der Bereich " + chDiff + " ist falsch.";
            } else {
              message = "Diagramm richtig.";
            }
          }
        }
      }
      return message;
    }
    
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
    if(conditionalFormating)
      sc.compareSheetConditionalFormatting();
    
    if(conditionalFormating)
      setCellComment(compareTable.getRow(0).getCell(0), sc.getMessage());
    
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
          setCellComment(cellCompare, cc.getMessage());
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
    if(table.getRow(row) != null)
      return table.getRow(row).getCell(column);
    else
      return null;
  }
  
  @Override
  protected void setCellComment(Cell cell, String message) {
    if(message == null || message.isEmpty())
      return;
    // Remove comment if exists
    if(cell.getCellComment() != null)
      cell.removeCellComment();
    
    // Create new drawing object
    Drawing drawing = cell.getSheet().createDrawingPatriarch();
    CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
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
  
  @Override
  protected String compareCellValues(Cell masterCell, Cell compareCell) {
    String cell1Value = getStringValueOfCell(masterCell);
    String cell2Value = getStringValueOfCell(compareCell);
    if(cell2Value.equals("")) {
      return "Keinen Wert angegeben!";
    } else if(cell1Value.equals(cell2Value)) {
      return "Wert richtig.";
    } else {
      return "Wert falsch. Erwartet wurde '" + cell1Value + "'.";
    }
  }
  
  @Override
  protected String compareCellFormulas(Cell masterCell, Cell compareCell) {
    // TODO Auto-generated method stub
    if(masterCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
      if(masterCell.toString().equals(compareCell.toString())) {
        return "Formel richtig.";
      } else {
        if(compareCell.getCellType() != Cell.CELL_TYPE_FORMULA) {
          return "Keine Formel angegeben!";
        } else {
          String string = StringHelper.getDiffOfTwoFormulas(masterCell.toString(), compareCell.toString());
          if(string.equals("")) {
            return "Formel richtig.";
          } else {
            return "Formel falsch. " + string;
          }
        }
      }
    } else
      return "";
    // return "Fehler!";
  }
  
  private static String getStringValueOfCell(Cell cell) {
    if(cell.getCellType() == Cell.CELL_TYPE_FORMULA)
      switch(cell.getCachedFormulaResultType()) {
      case Cell.CELL_TYPE_NUMERIC:
        return Double.toString(cell.getNumericCellValue());
      case Cell.CELL_TYPE_STRING:
        return cell.getRichStringCellValue().toString();
      default:
        return "";
      }
    else
      return cell.toString();
  }
  
}
