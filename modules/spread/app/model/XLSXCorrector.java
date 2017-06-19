package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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

import play.Logger;

/**
 *
 * @author Stefan Olbrecht
 *
 */
public class XLSXCorrector extends SpreadCorrector<Workbook, Sheet, XSSFCell, Font, Short> {
  
  private static final String FORMULA_CORRECT = "Formel richtig.";
  
  private static String getStringValueOfCell(Cell cell) {
    if(cell.getCellType() != Cell.CELL_TYPE_FORMULA)
      return cell.toString();
    switch(cell.getCachedFormulaResultType()) {
    case Cell.CELL_TYPE_NUMERIC:
      return Double.toString(cell.getNumericCellValue());
    case Cell.CELL_TYPE_STRING:
      return cell.getRichStringCellValue().toString();
    default:
      return "";
    }
  }
  
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
          HashSet<String> items1 = new HashSet<>();
          for(CellRangeAddress cf: format1.getFormattingRanges()) {
            items1.add(cf.formatAsString());
          }
          HashSet<String> items2 = new HashSet<>();
          for(CellRangeAddress cf: format2.getFormattingRanges()) {
            items2.add(cf.formatAsString());
          }
          String cfDiff = HashSetHelper.getSheetCFDiff(items2, items1);
          if(!cfDiff.isEmpty()) {
            message += "Bedingte Formatierung falsch.";
            message += " Der Bereich " + cfDiff + " ist falsch.\n";
          } else {
            String string1 = RegExpHelper.getExcelCFFormulaList(format1.toString());
            String string2 = RegExpHelper.getExcelCFFormulaList(format2.toString());
            String diff = HashSetHelper.getDiffOfTwoFormulas(string1, string2);
            if(diff.isEmpty()) {
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
  
  @Override
  public void closeDocument(Workbook document) {
    try {
      document.close();
    } catch (IOException e) {
      Logger.error("There has been an error closing a workbook", e);
    }
  }
  
  @Override
  public String compareCellFormulas(XSSFCell masterCell, XSSFCell compareCell) {
    if(masterCell.getCellType() != Cell.CELL_TYPE_FORMULA)
      return "Es war keine Formel anzugeben.";
    
    if(masterCell.toString().equals(compareCell.toString()))
      return FORMULA_CORRECT;
    
    if(compareCell.getCellType() != Cell.CELL_TYPE_FORMULA)
      return "Keine Formel angegeben!";
    
    String difference = HashSetHelper.getDiffOfTwoFormulas(masterCell.toString(), compareCell.toString());
    return difference.isEmpty() ? FORMULA_CORRECT : "Formel falsch. " + difference;
    
  }
  
  @Override
  public String compareCellValues(XSSFCell masterCell, XSSFCell compareCell) {
    String masterCellValue = getStringValueOfCell(masterCell);
    String compareCellValue = getStringValueOfCell(compareCell);
    if(compareCellValue.isEmpty())
      return "Keinen Wert angegeben!";
    else if(masterCellValue.equals(compareCellValue))
      return "Wert richtig.";
    else
      return "Wert falsch. Erwartet wurde '" + masterCellValue + "'.";
  }
  
  @Override
  public String compareChartsInSheet(Sheet compareSheet, Sheet sampleSheet) {
    XSSFDrawing sampleDrawing = ((XSSFSheet) sampleSheet).createDrawingPatriarch();
    XSSFDrawing compareDrawing = ((XSSFSheet) compareSheet).createDrawingPatriarch();
    int sampleChartCount = sampleDrawing.getCharts().size();
    int compareChartCount = compareDrawing.getCharts().size();
    
    if(sampleChartCount == 0)
      return "Es waren keine Diagramme zu erstellen.";
    
    if(sampleChartCount != compareChartCount)
      return "Falsche Anzahl an Diagrammen im Sheet (Erwartet: " + sampleChartCount + ", Gefunden: " + compareChartCount
          + ").";
    
    // TODO: refactor & test!
    StringBuilder message = new StringBuilder();
    for(int i = 0; i < sampleChartCount; i++) {
      
      CTChart chartMaster = sampleDrawing.getCharts().get(i).getCTChart();
      CTChart chartCompare = compareDrawing.getCharts().get(i).getCTChart();
      
      if(chartCompare == null)
        message.append("Sheet konnte nicht geöffnet werden!");
      else {
        message.append("Diagramm falsch.");
        String stringMaster = chartMaster.toString();
        String stringCompare = chartCompare.toString();
        // Compare Title
        String title1 = RegExpHelper.getExcelChartTitle(stringMaster);
        String title2 = RegExpHelper.getExcelChartTitle(stringCompare);
        if(!title1.equals(title2)) {
          message.append(" Der Titel sollte " + title1 + " lauten.");
        } else {
          // Compare ranges
          String chDiff = RegExpHelper.getExcelChartRangesDiff(sampleSheet.getSheetName(), stringMaster,
              compareSheet.getSheetName(), stringCompare);
          if(chDiff.isEmpty())
            message.append("Diagramm(e) richtig.");
          else
            message.append(" Folgende Bereiche sind falsch: " + chDiff);
        }
      }
    }
    return message.toString();
    
  }
  
  @Override
  public String compareNumberOfChartsInDocument(Workbook compareDocument, Workbook sampleDocument) {
    // TODO: wird nur von ODFCorrector benutzt!
    return null;
  }
  
  @Override
  public void compareSheet(Sheet sampleTable, Sheet compareTable, boolean conditionalFormating) {
    
    // Compare conditional formatting
    if(conditionalFormating) {
      String conditionalFormattingResult = compareSheetConditionalFormatting(sampleTable, compareTable);
      setCellComment((XSSFCell) compareTable.getRow(0).getCell(0), conditionalFormattingResult);
    }
    
    // Iterate over colored cells
    List<XSSFCell> range = getColoredRange(sampleTable);
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
          // TODO: Use enum instead of Strings!??
          if("Wert richtig.".equals(equalCell) && FORMULA_CORRECT.equals(equalFormula)) {
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
  public XSSFCell getCellByPosition(Sheet table, int row, int column) {
    if(table.getRow(row) != null)
      return (XSSFCell) table.getRow(row).getCell(column);
    else
      return null;
  }
  
  @Override
  public List<XSSFCell> getColoredRange(Sheet master) {
    List<XSSFCell> range = new ArrayList<>();
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
  public Sheet getSheetByIndex(Workbook document, int sheetIndex) {
    return document.getSheetAt(sheetIndex);
  }
  
  @Override
  public int getSheetCount(Workbook document) {
    return document.getNumberOfSheets();
  }
  
  @Override
  public Workbook loadDocument(Path path) {
    try {
      return new XSSFWorkbook(path.toFile());
    } catch (IOException | InvalidFormatException e) {
      Logger.error("There has been an error loading a XSSFWorkbook", e);
      return null;
    }
  }
  
  @Override
  public void saveCorrectedSpreadsheet(Workbook compareDocument, Path testPath) {
    // @formatter:off
    String fileNameNew =
        com.google.common.io.Files.getNameWithoutExtension(testPath.toString()) +
        CORRECTION_ADD_STRING + "." +
        com.google.common.io.Files.getFileExtension(testPath.toString());
    Path savePath = Paths.get(testPath.getParent().toString(), fileNameNew);
    // @formatter:on
    
    try {
      if(!savePath.getParent().toFile().exists())
        Files.createDirectories(savePath.getParent());
      
      FileOutputStream fileOut = new FileOutputStream(savePath.toFile());
      compareDocument.write(fileOut);
      fileOut.close();
    } catch (IOException e) {
      Logger.error("Fehler beim Speichern der korrigierten Datei!", e);
    }
  }
  
  @Override
  public void setCellComment(XSSFCell cell, String message) {
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
  public void setCellStyle(XSSFCell cell, Font font, Short color) {
    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
    
    // TODO: BOLD or ITALIC?
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
