package model.spreadsheet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
//TODO: Umstellung auf XSSFROW!
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
public class XLSXCorrector extends SpreadCorrector<Workbook, Sheet, XSSFCell, Font, Short> {
  
  protected static String compareSheetConditionalFormatting(Sheet master, Sheet compare) {
    String message = "";
    SheetConditionalFormatting scf1 = master.getSheetConditionalFormatting();
    SheetConditionalFormatting scf2 = compare.getSheetConditionalFormatting();
    int count1 = scf1.getNumConditionalFormattings();
    int count2 = scf2.getNumConditionalFormattings();
    
    if(count1 == 0)
      return "Keine bedingten Formatierungen erwartet.";
    if(count2 == 0)
      return "Bedingte Formatierung falsch. Keine Bedingte Formatierung gefunden.";
    if(count1 != count2)
      return "Bedingte Formatierung falsch. Zu wenig Bedingte Formatierungen (Erwartet: " + count1 + ", Gefunden: "
          + count2 + ").\n";
    
    for(int i = 0; i < count1; i++) {
      ConditionalFormatting format1 = scf1.getConditionalFormattingAt(i);
      ConditionalFormatting format2 = scf2.getConditionalFormattingAt(i);
      if(format2 != null) {
        if(format1.equals(format2)) {
          message += "Bedingte Formatierung richtig.\n";
        } else {
          // Compare ranges reference
          HashSet<String> items1 = new HashSet<String>();
          for(CellRangeAddress cf: format1.getFormattingRanges()) {
            items1.add(cf.formatAsString());
          }
          HashSet<String> items2 = new HashSet<String>();
          for(CellRangeAddress cf: format2.getFormattingRanges()) {
            items2.add(cf.formatAsString());
          }
          String cfDiff = HashSetHelper.getSheetCFDiff(items2, items1);
          if(!cfDiff.equals("")) {
            message += "Bedingte Formatierung falsch.";
            message += " Der Bereich " + cfDiff + " ist falsch.\n";
          } else {
            String string1 = RegExpHelper.getExcelCFFormulaList(format1.toString());
            String string2 = RegExpHelper.getExcelCFFormulaList(format2.toString());
            String diff = StringHelper.getDiffOfTwoFormulas(string1, string2);
            if(diff.equals("")) {
              message += "Bedingte Formatierung richtig.\n";
            } else {
              message += "Bedingte Formatierung falsch." + diff + "\n";
            }
          }
        }
      }
    }
    return message;
  }
  
  private static String getStringValueOfCell(Cell cell) {
    if(cell.getCellType() != Cell.CELL_TYPE_FORMULA)
      return cell.toString();
    switch(cell.getCachedFormulaResultType()) {
    case Cell.CELL_TYPE_BLANK:
      return "";
    case Cell.CELL_TYPE_NUMERIC:
      return Double.toString(cell.getNumericCellValue());
    case Cell.CELL_TYPE_STRING:
      return cell.getRichStringCellValue().toString();
    default:
      return "";
    }
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
  protected String compareCellFormulas(XSSFCell masterCell, XSSFCell compareCell) {
    // TODO Auto-generated method stub
    if(masterCell.getCellType() != Cell.CELL_TYPE_FORMULA)
      return "Es war keine Formel anzugeben.";
    
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
    // return "Fehler!";
  }
  
  @Override
  protected String compareCellValues(XSSFCell masterCell, XSSFCell compareCell) {
    String masterCellValue = getStringValueOfCell(masterCell);
    String compareCellValue = getStringValueOfCell(compareCell);
    if(compareCellValue.equals(""))
      return "Keinen Wert angegeben!";
    else if(masterCellValue.equals(compareCellValue))
      return "Wert richtig.";
    else
      return "Wert falsch. Erwartet wurde '" + masterCellValue + "'.";
  }
  
  @Override
  protected String compareChartsInSheet(Sheet compareSheet, Sheet sampleSheet) {
    XSSFDrawing sampleDrawing = ((XSSFSheet) sampleSheet).createDrawingPatriarch();
    XSSFDrawing compareDrawing = ((XSSFSheet) compareSheet).createDrawingPatriarch();
    int sampleChartCount = sampleDrawing.getCharts().size();
    int compareChartCount = compareDrawing.getCharts().size();
    
    if(sampleChartCount == 0)
      return "Es waren keine Diagramme zu erstellen.";
    
    if(sampleChartCount != compareChartCount)
      return "Falsche Anzahl an Diagrammen im Sheet (Erwartet: " + sampleChartCount + ", Gefunden: "
          + compareChartCount + ").";
    
    // FIXME: refactor & test!
    String message = "";
    for(int i = 0; i < sampleChartCount; i++) {
      CTChart chartMaster = sampleDrawing.getCharts().get(i).getCTChart();
      CTChart chartCompare = compareDrawing.getCharts().get(i).getCTChart();
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
          String chDiff = RegExpHelper.getExcelChartRangesDiff(sampleSheet.getSheetName(), stringMaster,
              compareSheet.getSheetName(), stringCompare);
          if(chDiff != "") {
            message += " Folgende Bereiche sind falsch: " + chDiff;
          } else {
            message = "Diagramm(e) richtig.";
          }
        }
      }
    }
    return message;
    
  }
  
  @Override
  protected String compareNumberOfChartsInDocument(Workbook compareDocument, Workbook sampleDocument) {
    // FIXME: wird nur von ODFCorrector benutzt!
    return null;
  }
  
  @Override
  protected void compareSheet(Sheet sampleTable, Sheet compareTable, boolean conditionalFormating) {
    
    // Compare conditional formatting
    if(conditionalFormating) {
      String conditionalFormattingResult = compareSheetConditionalFormatting(sampleTable, compareTable);
      setCellComment((XSSFCell) compareTable.getRow(0).getCell(0), conditionalFormattingResult);
    }
    
    // Iterate over colored cells
    ArrayList<XSSFCell> range = getColoredRange(sampleTable);
    for(Cell cellMaster: range) {
      int rowIndex = cellMaster.getRowIndex();
      int columnIndex = cellMaster.getColumnIndex();
      Row rowCompare = compareTable.getRow(rowIndex);
      if(rowCompare != null) {
        XSSFCell cellCompare = (XSSFCell) rowCompare.getCell(columnIndex);
        if(cellCompare != null) {
          // Create CellComparator
          cellCompare.setCellComment(null);
          // Compare cell values
          String equalCell = compareCellValues((XSSFCell) cellMaster, cellCompare);
          String equalFormula = compareCellFormulas((XSSFCell) cellMaster, cellCompare);
          setCellComment(cellCompare, equalCell + "\n" + equalFormula);
          // FIXME
          if(equalCell.equals("Wert richtig.") && equalFormula.equals("Formel richtig.")) {
            // Style green
            setCellStyle(cellCompare, compareTable.getWorkbook().createFont(), IndexedColors.GREEN.getIndex());
          } else {
            // Style red
            setCellStyle(cellCompare, compareTable.getWorkbook().createFont(), IndexedColors.RED.getIndex());
          }
        }
      }
    }
    
  }
  
  @Override
  protected XSSFCell getCellByPosition(Sheet table, int row, int column) {
    if(table.getRow(row) != null)
      return (XSSFCell) table.getRow(row).getCell(column);
    else
      return null;
  }
  
  @Override
  protected ArrayList<XSSFCell> getColoredRange(Sheet master) {
    ArrayList<XSSFCell> range = new ArrayList<XSSFCell>();
    for(Row row: master) {
      for(Cell cell: row) {
        Color foreground = cell.getCellStyle().getFillForegroundColorColor();
        Color background = cell.getCellStyle().getFillBackgroundColorColor();
        if(foreground != null && background != null)
          range.add((XSSFCell) cell);
      }
    }
    return range;
  }
  
  @Override
  protected Sheet getSheetByIndex(Workbook document, int sheetIndex) {
    return document.getSheetAt(sheetIndex);
  }
  
  @Override
  protected int getSheetCount(Workbook document) {
    return document.getNumberOfSheets();
  }
  
  @Override
  protected Workbook loadDocument(Path path) {
    // FIXME: differenziere zwichen verschiedenen Fehlergründen! ==> Bessere
    // Rückmeldung?
    try {
      return new XSSFWorkbook(path.toFile());
    } catch (Exception e) {
      return null;
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
  protected void setCellComment(XSSFCell cell, String message) {
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
  protected void setCellStyle(XSSFCell cell, Font font, Short color) {
    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
    
    // FIXME: BOLD or ITALIC?
    // if(bool)
    // font.setBold(true);
    // else
    // font.setItalic(true);
    font.setColor(color);
    style.setFont(font);
    
    style.setAlignment(cell.getCellStyle().getAlignment());
    style.setDataFormat(cell.getCellStyle().getDataFormat());
    style.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
    style.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
    cell.setCellStyle(style);
  }
  
}
