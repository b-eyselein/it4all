package model.spread

import java.io.FileOutputStream
import java.nio.file.{Files, Path, Paths}

import com.google.common.io.{Files => GFiles}
import model.spread.SpreadConsts._
import model.spread.SpreadUtils._
import org.odftoolkit.odfdom.`type`.Color
import org.odftoolkit.simple.SpreadsheetDocument
import org.odftoolkit.simple.style.Font
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle
import org.odftoolkit.simple.table.{Cell, Table}

import scala.util.Try

object ODFCorrector extends SpreadCorrector[SpreadsheetDocument, Table, Cell, Font, Color](Color.GREEN, Color.RED) {

  val maxRow    = 80
  val maxColumn = 22

  val colorWhite       = "#FFFFFF"
  val fontName         = "Arial"
  val fontSize: Double = 10

  override def closeDocument(document: SpreadsheetDocument): Unit = document.close()

  override def compareCellFormulas(masterCell: Cell, compareCell: Cell): (Boolean, String) = Option(masterCell.getFormula) match {
    case None                => (true, noFormulaRequired)
    case Some(masterFormula) => Option(compareCell.getFormula) match {
      case None                 => (false, formulaMissing)
      case Some(compareFormula) =>
        if (masterFormula == compareFormula) (true, formulaCorrect)
        else {
          val diffOfTwoFormulas = SpreadUtils.getDiffOfTwoFormulas(masterFormula, compareFormula)
          if (diffOfTwoFormulas.isEmpty) (true, formulaCorrect)
          else (false, s"Formel falsch. $diffOfTwoFormulas")
        }
    }
  }

  override def compareCellValues(masterCell: Cell, compareCell: Cell): (Boolean, String) = {
    val masterValue = masterCell.getStringValue
    val compareCellValue = compareCell.getStringValue
    val compareValue = if (compareCellValue.contains('\n')) compareCellValue.substring(0, compareCellValue.indexOf('\n')) else compareCellValue

    if (compareValue.isEmpty) (false, COMMENT_VALUE_MISSING)
    else if (masterValue == compareValue) (true, COMMENT_VALUE_CORRECT)
    else (false, s"Wert falsch. Erwartet wurde '$masterValue'.")
  }

  override def compareChartsInSheet(compareSheet: Table, sampleSheet: Table): (Boolean, String) = (false, "") // FIXME: nicht von ODFToolkit unterstützt...

  override def compareNumberOfChartsInDocument(compare: SpreadsheetDocument, sample: SpreadsheetDocument): (Boolean, String) =
    sample.getChartCount match {
      case 0           => (false, COMMENT_CHART_FALSE)
      case sampleCount =>
        val compareCount = compare.getChartCount
        if (sampleCount == compareCount) (true, COMMENT_CHART_NUM_CORRECT)
        else (false, s"Falsche Anzahl Diagramme im Dokument (Erwartet: $sampleCount, Gefunden: $compareCount).")
    }

  override def compareSheet(sampleTable: Table, compareTable: Table, conditionalFormating: Boolean): Unit = {

    if (conditionalFormating) {
      val conditionalFormattingResult = compareSheetConditionalFormatting(sampleTable, compareTable)
      getCellByPosition(compareTable, 0, 0) match {
        case None       => Unit
        case Some(cell) => setCellComment(cell, conditionalFormattingResult.mkString("\n"))
      }
    }

    for (cellMaster <- getColoredRange(sampleTable)) {
      getCellByPosition(compareTable, cellMaster.getRowIndex, cellMaster.getColumnIndex) match {
        case None              => Unit
        case Some(cellCompare) =>

          val (cellValueResult, cellValueMsg) = compareCellValues(cellMaster, cellCompare)

          val (cellFormulaResult, cellFormulaMsg) = compareCellFormulas(cellMaster, cellCompare)

          setCellComment(cellCompare, cellValueMsg + "\n" + cellFormulaMsg)

          val fontstyle = if (cellValueResult && cellFormulaResult) FontStyle.BOLD else FontStyle.ITALIC

          val font = new Font(fontName, fontstyle, fontSize)

          setCellStyle(cellCompare, font, cellValueResult && cellFormulaResult)
      }
    }
  }

  override def getCellByPosition(table: Table, column: Int, row: Int): Option[Cell] = Option(table.getCellByPosition(column, row))

  override def getColoredRange(master: Table): List[Cell] =
    (for {row <- 0 until maxRow
          column <- 0 until maxColumn
          oCell = master.getRowByIndex(row).getCellByIndex(column)
          if oCell.getCellBackgroundColorString != colorWhite} yield oCell).toList


  override def compareSheetConditionalFormatting(master: Table, compare: Table): List[String] = List.empty

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

  override def setCellComment(cell: Cell, message: String): Unit = if (message != null && message.nonEmpty) {
    cell.setNoteText(message)
  }

  override def setCellStyle(cell: Cell, font: Font, success: Boolean): Unit = {
    font.setColor(if (success) green else red)
    cell.setFont(font)
  }

}
