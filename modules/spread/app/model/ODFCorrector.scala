package model

import java.io.FileOutputStream
import java.nio.file.{Files, Path, Paths}

import com.google.common.io.{Files => GFiles}
import model.StringConsts._
import org.odftoolkit.odfdom.`type`.Color
import org.odftoolkit.simple.SpreadsheetDocument
import org.odftoolkit.simple.style.Font
import org.odftoolkit.simple.table.{Cell, Table}

import scala.util.Try


object ODFCorrector extends SpreadCorrector[SpreadsheetDocument, Table, Cell, Font, Color] {

  val MAXROW = 80
  val MAXCOLUMN = 22

  val COLOR_WHITE = "#FFFFFF"
  val FONT = "Arial"
  val FONT_SIZE: Double = 10

  override def closeDocument(document: SpreadsheetDocument): Unit = document.close()

  override def compareCellFormulas(masterCell: Cell, compareCell: Cell): (Boolean, String) = Option(masterCell.getFormula) match {
    case None => (true, "")
    case Some(masterFormula) => Option(compareCell.getFormula) match {
      case None => (false, COMMENT_FORMULA_MISSING)
      case Some(compareFormula) =>
        if (masterFormula == compareFormula) (true, COMMENT_FORMULA_CORRECT)
        else {
          val diffOfTwoFormulas = SpreadUtils.getDiffOfTwoFormulas(masterFormula, compareFormula)
          if (diffOfTwoFormulas.isEmpty) (true, COMMENT_FORMULA_CORRECT)
          else (false, s"Formel falsch. $diffOfTwoFormulas")
        }
    }
  }

  override def compareCellValues(masterCell: Cell, compareCell: Cell): (Boolean, String) = {
    //    String masterValue = masterCell.getStringValue()
    //    String compareValue = compareCell.getStringValue()
    //    // FIXME: why substring from 0 to first newline?
    //    if (compareValue.indexOf('\n') != -1)
    //      compareValue = compareValue.substring(0, compareValue.indexOf('\n'))
    //    if (compareValue.isEmpty())
    //      return COMMENT_VALUE_MISSING
    //    else if (masterValue.equals(compareValue))
    //      return COMMENT_VALUE_CORRECT
    //    else
    //          return String.format(COMMENT_VALUE_INCORRECT_VAR, masterValue)
    (false, "TODO...")
  }

  override def compareChartsInSheet(compareSheet: Table, sampleSheet: Table): (Boolean, String) = {
    // FIXME: nicht von ODFToolkit unterstützt...
    null
  }

  override def compareNumberOfChartsInDocument(compare: SpreadsheetDocument, sample: SpreadsheetDocument): (Boolean, String) =
    sample.getChartCount match {
      case 0 => (false, COMMENT_CHART_FALSE)
      case sampleCount =>
        val compareCount = compare.getChartCount
        if (sampleCount == compareCount) (true, COMMENT_CHART_NUM_CORRECT)
        else (false, s"Falsche Anzahl Diagramme im Dokument (Erwartet: $sampleCount, Gefunden: $compareCount).")
    }

  override def compareSheet(sampleTable: Table, compareTable: Table, correctConditionalFormating: Boolean): Unit = {
    //    if (correctConditionalFormating) {
    //      // NOTICE: Does not work in ODF Toolkit
    //    }
    //    // Iterate over colored cells
    //    List<Cell> range = getColoredRange(sampleTable)
    //    for (Cell cellMaster : range) {
    //      int rowIndex = cellMaster.getRowIndex()
    //      int columnIndex = cellMaster.getColumnIndex()
    //      Cell cellCompare = compareTable.getCellByPosition(columnIndex, rowIndex)
    //
    //      if (cellCompare == null)
    //	// TODO: Fehler werfen? Kann das überhaupt passieren?
    //	return
    //
    //      // Compare cell values
    //      String cellValueResult = compareCellValues(cellMaster, cellCompare)
    //      String cellFormulaResult = compareCellFormulas(cellMaster, cellCompare)
    //
    //      setCellComment(cellCompare, cellValueResult + "\n" + cellFormulaResult)
    //
    //      // FIXME: use enum instead of String!
    //      if (COMMENT_VALUE_CORRECT.equals(cellValueResult)
    //          && (cellFormulaResult.isEmpty() || COMMENT_FORMULA_CORRECT.equals(cellFormulaResult)))
    //	setCellStyle(cellCompare, new Font(FONT, FontStyle.BOLD, FONT_SIZE), Color.GREEN)
    //      // cellCompare.setFont(new Font(FONT, FontStyle.BOLD, FONT_SIZE,
    //      // Color.GREEN))
    //      else
    //	setCellStyle(cellCompare, new Font(FONT, FontStyle.ITALIC, FONT_SIZE), Color.RED)
    //      // cellCompare.setFont(new Font(FONT, FontStyle.ITALIC, FONT_SIZE,
    //      // Color.RED))
    //    }
  }

  override def getCellByPosition(table: Table, column: Int, row: Int): Cell = table.getCellByPosition(column, row)

  //  @SuppressWarnings("deprecation")
  override def getColoredRange(master: Table): List[Cell] = {
    //    List<Cell> range = new ArrayList<>()
    //    for (int row = 0 row < MAXROW row++) {
    //      for (int column = 0 column < MAXCOLUMN column++) {
    //	Cell oCell = master.getRowByIndex(row).getCellByIndex(column)
    //	if (!oCell.getCellBackgroundColorString().equals(COLOR_WHITE))
    //	  range.add(oCell)
    //      }
    //    }
    //    return range
    List.empty
  }

  override def getSheetByIndex(document: SpreadsheetDocument, sheetIndex: Int): Table = document.getSheetByIndex(sheetIndex)

  override def getSheetCount(document: SpreadsheetDocument): Int = document.getSheetCount

  override def loadDocument(path: Path): Try[SpreadsheetDocument] = Try(SpreadsheetDocument.loadDocument(path.toFile))

  override def saveCorrectedSpreadsheet(compareDocument: SpreadsheetDocument, testPath: Path): Try[Path] = {
    val tPath = testPath.toString
    val fileNameNew = GFiles.getNameWithoutExtension(tPath) + CORRECTION_ADD_STRING + "." + GFiles.getFileExtension(tPath)
    val savePath = Paths.get(testPath.getParent.toString, fileNameNew)
    Try({
      if (!savePath.getParent.toFile.exists())
        Files.createDirectories(savePath.getParent)

      val fileOut: FileOutputStream = new FileOutputStream(savePath.toFile)
      compareDocument.save(fileOut)
      fileOut.close()

      savePath
    })
  }

  override def setCellComment(cell: Cell, message: String): Unit = {
    if (message != null || message.nonEmpty)
      cell.setNoteText(message)
  }

  override def setCellStyle(cell: Cell, font: Font, color: Color): Unit = {
    font.setColor(color)
    cell.setFont(font)
  }

}
