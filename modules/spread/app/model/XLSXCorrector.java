package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
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

  private static boolean compareChart(Sheet compareSheet, Sheet sampleSheet, List<String> messages, CTChart chartMaster,
      CTChart chartCompare) throws CorrectionException {
    if (chartCompare == null)
      throw new CorrectionException("", StringConsts.ERROR_LOAD_SHEET);

    String stringMaster = chartMaster.toString();
    String stringCompare = chartCompare.toString();
    // Compare Title
    String title1 = RegExpHelper.getExcelChartTitle(stringMaster);
    String title2 = RegExpHelper.getExcelChartTitle(stringCompare);

    if (!title1.equals(title2)) {
      messages.add(String.format(StringConsts.COMMENT_SHEET_TITLE_INCORRECT_VAR, title1));
      return false;
    }

    // Compare ranges
    String chDiff = RegExpHelper.getExcelChartRangesDiff(sampleSheet.getSheetName(), stringMaster,
        compareSheet.getSheetName(), stringCompare);
    if (!chDiff.isEmpty()) {
      messages.add(" " + String.format(StringConsts.COMMENT_CHART_RANGE_INCORRECT_VAR, chDiff));
      return false;
    }
    return true;
  }

  private static String compareConditionalFormattings(ConditionalFormatting format1, ConditionalFormatting format2) {
    if (format2 == null)
      return "";

    if (format1.equals(format2))
      return StringConsts.COMMENT_CONDITIONAL_FORMATTING_CORRECT + "\n";

    // Compare ranges reference
    String cfDiff = HashSetHelper.getSheetCFDiff(getFormatStrings(format2), getFormatStrings(format1));
    if (!cfDiff.isEmpty())
      return StringConsts.COMMENT_CONDITIONAL_FORMATTING_INCORRECT + " "
          + String.format(StringConsts.COMMENT_CONDITIONAL_FORMATTING_RANGE_VAR, cfDiff) + "\n";

    String string1 = RegExpHelper.getExcelCFFormulaList(format1.toString());
    String string2 = RegExpHelper.getExcelCFFormulaList(format2.toString());
    String diff = HashSetHelper.getDiffOfTwoFormulas(string1, string2);

    if (diff.isEmpty())
      return StringConsts.COMMENT_CONDITIONAL_FORMATTING_CORRECT + "\n";
    else
      return StringConsts.COMMENT_CONDITIONAL_FORMATTING_INCORRECT + " "
          + String.format(StringConsts.COMMENT_CONDITIONAL_FORMATTING_DIFF_VAR, diff) + "\n";
  }

  private static Set<String> getFormatStrings(ConditionalFormatting format) {
    return Arrays.stream(format.getFormattingRanges()).map(CellRangeAddress::formatAsString)
        .collect(Collectors.toSet());
  }

  private static String getStringValueOfCell(Cell cell) {
    if (cell.getCellTypeEnum() != CellType.FORMULA)
      return cell.toString();

    switch (cell.getCachedFormulaResultTypeEnum()) {
    case NUMERIC:
      return Double.toString(cell.getNumericCellValue());
    case STRING:
      return cell.getRichStringCellValue().toString();
    default:
      return "";
    }
  }

  protected static List<String> compareSheetConditionalFormatting(Sheet master, Sheet compare) {
    SheetConditionalFormatting scf1 = master.getSheetConditionalFormatting();
    SheetConditionalFormatting scf2 = compare.getSheetConditionalFormatting();

    int count1 = scf1.getNumConditionalFormattings();
    int count2 = scf2.getNumConditionalFormattings();

    if (count1 == 0)
      return Arrays.asList(StringConsts.COMMENT_CONDITIONAL_FORMATTING_NUM_FALSE);

    if (count2 == 0)
      return Arrays.asList(StringConsts.COMMENT_CONDITIONAL_FORMATTING_NUM_INCORRECT);

    if (count1 != count2)
      return Arrays.asList(String.format(StringConsts.COMMENT_CONDITIONAL_FORMATTING_NUM_INCORRECT_VAR, count1, count2) + "\n");

    return IntStream.range(0, count1)
        .mapToObj(
            i -> compareConditionalFormattings(scf1.getConditionalFormattingAt(i), scf2.getConditionalFormattingAt(i)))
        .collect(Collectors.toList());
  }

  @Override
  public void closeDocument(Workbook document) {
    try {
      document.close();
    } catch (IOException e) {
      Logger.error(StringConsts.ERROR_CLOSE_FILE, e);
    }
  }

  @Override
  public String compareCellFormulas(XSSFCell masterCell, XSSFCell compareCell) {
    if (masterCell.getCellTypeEnum() != CellType.FORMULA)
      return StringConsts.COMMENT_FORMULA_FALSE;

    if (masterCell.toString().equals(compareCell.toString()))
      return StringConsts.COMMENT_FORMULA_CORRECT;

    if (compareCell.getCellTypeEnum() != CellType.FORMULA)
      return StringConsts.COMMENT_FORMULA_MISSING;

    String difference = HashSetHelper.getDiffOfTwoFormulas(masterCell.toString(), compareCell.toString());
    return difference.isEmpty() ? StringConsts.COMMENT_FORMULA_CORRECT
        : String.format(StringConsts.COMMENT_FORMULA_INCORRECT_VAR, difference);

  }

  @Override
  public String compareCellValues(XSSFCell masterCell, XSSFCell compareCell) {
    String masterCellValue = getStringValueOfCell(masterCell);
    String compareCellValue = getStringValueOfCell(compareCell);

    if (compareCellValue.isEmpty())
      return StringConsts.COMMENT_VALUE_MISSING;
    else if (masterCellValue.equals(compareCellValue))
      return StringConsts.COMMENT_VALUE_CORRECT;
    else
      return String.format(StringConsts.COMMENT_VALUE_INCORRECT_VAR, masterCellValue);
  }

  @Override
  public String compareChartsInSheet(Sheet compareSheet, Sheet sampleSheet) {
    XSSFDrawing sampleDrawing = ((XSSFSheet) sampleSheet).createDrawingPatriarch();
    XSSFDrawing compareDrawing = ((XSSFSheet) compareSheet).createDrawingPatriarch();
    int sampleChartCount = sampleDrawing.getCharts().size();
    int compareChartCount = compareDrawing.getCharts().size();

    if (sampleChartCount == 0)
      return StringConsts.COMMENT_CHART_FALSE;
    else if (sampleChartCount != compareChartCount)
      return String.format(StringConsts.COMMENT_CHART_NUM_INCORRECT_VAR, sampleChartCount, compareChartCount);
    // TODO: refactor & test!
    boolean allChartsCorrect = true;
    List<String> messages = new LinkedList<>();
    for (int i = 0; i < sampleChartCount; i++) {

      CTChart chartMaster = sampleDrawing.getCharts().get(i).getCTChart();
      CTChart chartCompare = compareDrawing.getCharts().get(i).getCTChart();

      try {
	allChartsCorrect &= compareChart(compareSheet, sampleSheet, messages, chartMaster, chartCompare);
      } catch (CorrectionException e) {
	return StringConsts.ERROR_LOAD_CHART;
      }
    }
    if (allChartsCorrect)
      return StringConsts.COMMENT_CHART_CORRECT;
    else
      return String.join("\n", messages);

  }

  @Override
  public String compareNumberOfChartsInDocument(Workbook compareDocument, Workbook sampleDocument) {
    // TODO: wird nur von ODFCorrector benutzt!
    return null;
  }

  @Override
  public void compareSheet(Sheet sampleTable, Sheet compareTable, boolean conditionalFormating) {

    // Compare conditional formatting
    if (conditionalFormating) {
      List<String> conditionalFormattingResult = compareSheetConditionalFormatting(sampleTable, compareTable);
      setCellComment((XSSFCell) compareTable.getRow(0).getCell(0), String.join("\n", conditionalFormattingResult));
    }

    // Iterate over colored cells
    List<XSSFCell> range = getColoredRange(sampleTable);
    for (Cell cellMaster : range) {
      int rowIndex = cellMaster.getRowIndex();
      int columnIndex = cellMaster.getColumnIndex();

      Row rowCompare = compareTable.getRow(rowIndex);
      if (rowCompare == null)
	continue;

      XSSFCell cellCompare = (XSSFCell) rowCompare.getCell(columnIndex);
      if (cellCompare == null)
	continue;

      // Create CellComparator
      cellCompare.setCellComment(null);
      // Compare cell values
      String equalCell = compareCellValues((XSSFCell) cellMaster, cellCompare);
      String equalFormula = compareCellFormulas((XSSFCell) cellMaster, cellCompare);
      setCellComment(cellCompare, equalCell + "\n" + equalFormula);

      // TODO: Use enum instead of Strings!??
      short colorIndex = IndexedColors.RED.getIndex();
      if (StringConsts.COMMENT_VALUE_CORRECT.equals(equalCell)
          && StringConsts.COMMENT_FORMULA_CORRECT.equals(equalFormula))
	setCellStyle(cellCompare, compareTable.getWorkbook().createFont(), IndexedColors.GREEN.getIndex());

      setCellStyle(cellCompare, compareTable.getWorkbook().createFont(), colorIndex);
    }

  }

  @Override
  public XSSFCell getCellByPosition(Sheet table, int row, int column) {
    if (table.getRow(row) != null)
      return (XSSFCell) table.getRow(row).getCell(column);
    else
      return null;
  }

  @Override
  public List<XSSFCell> getColoredRange(Sheet master) {
    List<XSSFCell> range = new ArrayList<>();
    for (Row row : master) {
      for (Cell cell : row) {
	if (cell.getCellStyle().getFillForegroundColorColor() != null
	    && cell.getCellStyle().getFillBackgroundColorColor() != null)
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
  public Workbook loadDocument(Path path) throws CorrectionException {
    try {
      return new XSSFWorkbook(path.toFile());
    } catch (IOException | InvalidFormatException | InvalidOperationException e) {
      throw new CorrectionException("", String.format(StringConsts.ERROR_LOAD_VAR, path.getFileName()), e);
    }
  }

  @Override
  public void saveCorrectedSpreadsheet(Workbook compareDocument, Path testPath) {
    // @formatter:off
    String fileNameNew = com.google.common.io.Files.getNameWithoutExtension(testPath.toString())
        + StringConsts.CORRECTION_ADD_STRING + "." + com.google.common.io.Files.getFileExtension(testPath.toString());
    Path savePath = Paths.get(testPath.getParent().toString(), fileNameNew);
    // @formatter:on
    try {
      if (!savePath.getParent().toFile().exists())
	Files.createDirectories(savePath.getParent());
      FileOutputStream fileOut = new FileOutputStream(savePath.toFile());
      compareDocument.write(fileOut);
      fileOut.close();
    } catch (IOException e) {
      Logger.error(StringConsts.ERROR_SAVE_FILE, e);
    }
  }

  @Override
  public void setCellComment(XSSFCell cell, String message) {
    if (message == null || message.isEmpty())
      return;
    // Remove comment if exists
    if (cell.getCellComment() != null)
      cell.removeCellComment();

    // Create new drawing object
    XSSFDrawing drawing = cell.getSheet().createDrawingPatriarch();
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

    style.setAlignment(cell.getCellStyle().getAlignmentEnum());
    style.setDataFormat(cell.getCellStyle().getDataFormat());

    style.setBorderLeft(BorderStyle.MEDIUM);
    style.setBorderBottom(BorderStyle.MEDIUM);
    style.setBorderRight(BorderStyle.MEDIUM);
    style.setBorderTop(BorderStyle.MEDIUM);

    cell.setCellStyle(style);
  }

}
