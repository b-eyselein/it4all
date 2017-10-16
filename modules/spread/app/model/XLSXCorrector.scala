package model

import java.io.{FileOutputStream, IOException}
import java.nio.file._

import com.google.common.io.{Files => GFiles}
import model.SpreadUtils._
import model.StringConsts._
import org.apache.poi.ss.usermodel.CellType._
import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel._
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart
import play.Logger

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.Try

object XLSXCorrector extends SpreadCorrector[Workbook, Sheet, XSSFCell, Font, Short](IndexedColors.RED.getIndex, IndexedColors.GREEN.getIndex) {

  private def compareChart(compSheetName: String, sampSheetName: String, chartMaster: CTChart, chartCompareOpt: Option[CTChart]): (Boolean, String) = chartCompareOpt match {
    case None => (false, ERROR_LOAD_SHEET)
    case Some(chartCompare) =>
      val (stringMaster, stringCompare) = (chartMaster.toString, chartCompare.toString)

      val (expectedTitle, actualTitle) = (getExcelChartTitle(stringMaster), getExcelChartTitle(stringCompare))
      if (expectedTitle != actualTitle)
        (false, s"Der Titel des Tabellenblatts sollte '$expectedTitle' sein, war aber '$actualTitle'.")
      else {
        // Compare ranges
        val chDiff = getExcelChartRangesDiff(sampSheetName, stringMaster, compSheetName, stringCompare)
        if (chDiff.isEmpty) (true, "")
        else (false, s" Folgende Bereiche sind falsch: $chDiff")
      }
  }

  private def compareConditionalFormattings(format1: ConditionalFormatting, format2Opt: Option[ConditionalFormatting]): String = format2Opt match {
    case None => ""
    case Some(format2) =>
      if (format1 == format2) COMMENT_CONDITIONAL_FORMATTING_CORRECT + "\n"
      else {
        // Compare ranges reference
        val condformattingDiff: String = getSheetCFDiff(getFormatStrings(format2), getFormatStrings(format1))
        if (condformattingDiff.nonEmpty) {
          s"Bedingte Formatierung falsch. Der Bereich [$condformattingDiff] ist falsch.\n"
        } else {

          //          val diff: String = getDiffOfTwoFormulas(getExcelCFFormulaList(format1.toString), getExcelCFFormulaList(format2.toString))
          /*if (diff.isEmpty)*/ "Bedingte Formatierung richtig.\n"
          //          else s"Bedingte Formatierung falsch. ($diff).\n"
        }
      }
  }

  private def getFormatStrings(format: ConditionalFormatting): Set[String] = format.getFormattingRanges.map(_.formatAsString).toSet

  private def getStringValueOfCell(cell: Cell): String = if (cell.getCellTypeEnum == CellType.FORMULA)
    cell.getCachedFormulaResultTypeEnum match {
      case NUMERIC => java.lang.Double.toString(cell.getNumericCellValue)
      case STRING => cell.getRichStringCellValue.toString
      case _ => ""
    } else cell.toString


  override protected def compareSheetConditionalFormatting(master: Sheet, compare: Sheet): List[String] = {
    val scf1: SheetConditionalFormatting = master.getSheetConditionalFormatting
    val scf2: SheetConditionalFormatting = compare.getSheetConditionalFormatting

    scf1.getNumConditionalFormattings match {
      case 0 => List(COMMENT_CONDITIONAL_FORMATTING_NUM_FALSE)

      case count1 => scf2.getNumConditionalFormattings match {
        case 0 => List(COMMENT_CONDITIONAL_FORMATTING_NUM_INCORRECT)

        case count2 =>
          if (count1 != count2) List(s"Bedingte Formatierung falsch. Fehlende bedingte Formatierungen (Erwartet: $count1, Gefunden: $count2).\n")
          else (0 to count1).map(i => compareConditionalFormattings(scf1.getConditionalFormattingAt(i), Option(scf2.getConditionalFormattingAt(i)))).toList
      }
    }
  }

  override def closeDocument(document: Workbook): Unit = {
    try {
      document.close()
    } catch {
      case e: IOException => Logger.error(ERROR_CLOSE_FILE, e)
    }
  }

  override def compareCellFormulas(masterCell: XSSFCell, compareCell: XSSFCell): (Boolean, String) =
    if (masterCell.getCellTypeEnum != CellType.FORMULA) (false, "Keine Formel notwendig.")
    else {
      if (masterCell.toString == compareCell.toString) (true, formulaCorrect)
      else if (compareCell.getCellTypeEnum != CellType.FORMULA) (false, "Formel notwendig.")
      else {
        val difference: String = getDiffOfTwoFormulas(masterCell.toString, compareCell.toString)

        if (difference.isEmpty) (true, COMMENT_FORMULA_CORRECT)
        else (false, s"Formel falsch. $difference")
      }
    }


  override def compareCellValues(masterCell: XSSFCell, compareCell: XSSFCell): (Boolean, String) = {
    val masterCellValue: String = getStringValueOfCell(masterCell)
    val compareCellValue: String = getStringValueOfCell(compareCell)

    if (compareCellValue.isEmpty) (false, COMMENT_VALUE_MISSING)
    else if (masterCellValue != compareCellValue) (false, s"Wert falsch. Erwartet wurde '$masterCellValue'.")
    else (true, COMMENT_VALUE_CORRECT)
  }

  private def getCharts(sheet: Sheet): List[XSSFChart] = sheet.asInstanceOf[XSSFSheet].createDrawingPatriarch().getCharts.asScala.toList

  override def compareChartsInSheet(compareSheet: Sheet, sampleSheet: Sheet): (Boolean, String) = getCharts(sampleSheet) match {
    case Nil => (false, COMMENT_CHART_FALSE)
    case sampleCharts =>
      val compareCharts = getCharts(compareSheet)

      if (sampleCharts.size != compareCharts.size)
        (false, s"Falsche Anzahl Diagramme im Dokument (Erwartet: ${sampleCharts.size}, Gefunden: ${compareCharts.size}).")
      else {
        // TODO: refactor & test!
        var allChartsCorrect: Boolean = true
        val messages: ListBuffer[String] = ListBuffer.empty

        for ((sampChart, compChart) <- sampleCharts.zip(compareCharts)) {
          val (chartSuc, chartMsg) = compareChart(compareSheet.getSheetName, sampleSheet.getSheetName, sampChart.getCTChart, Option(compChart.getCTChart))
          allChartsCorrect &= chartSuc
          messages += chartMsg
        }

        if (allChartsCorrect) (true, COMMENT_CHART_CORRECT)
        else (false, messages.mkString("\n"))
      }
  }


  override def compareNumberOfChartsInDocument(compareDocument: Workbook, sampleDocument: Workbook): (Boolean, String) = (true, "")

  override def compareSheet(sampleTable: Sheet, compareTable: Sheet, conditionalFormating: Boolean): Unit = {

    if (conditionalFormating) {
      val conditionalFormattingResult = compareSheetConditionalFormatting(sampleTable, compareTable)
      getCellByPosition(compareTable, 0, 0) match {
        case None => Unit
        case Some(cell) => setCellComment(cell, conditionalFormattingResult.mkString("\n"))
      }
    }

    for (cellMaster <- getColoredRange(sampleTable)) {
      getCellByPosition(compareTable, cellMaster.getRowIndex, cellMaster.getColumnIndex) match {
        case None => Unit
        case Some(cellCompare) =>

          val (cellsValueResult, cellsValueMsg) = compareCellValues(cellMaster.asInstanceOf[XSSFCell], cellCompare)

          val (cellFormulaSuc, cellFormulaMsg) = compareCellFormulas(cellMaster.asInstanceOf[XSSFCell], cellCompare)

          setCellComment(cellCompare, cellsValueMsg + "\n" + cellFormulaMsg)

          setCellStyle(cellCompare, compareTable.getWorkbook.createFont(), cellFormulaSuc && cellFormulaSuc)
      }
    }
  }

  override def getCellByPosition(table: Sheet, rowNum: Int, colNum: Int): Option[XSSFCell] =
    Option(table.getRow(rowNum)).map(_.getCell(colNum) match {
      case c: XSSFCell => c
      case _ => null
    })


  override def getColoredRange(master: Sheet): List[XSSFCell] =
    (for {
      row <- master.rowIterator.asScala
      cell <- row.cellIterator.asScala
      if cell.getCellStyle.getFillForegroundColorColor != null && cell.getCellStyle.getFillBackgroundColorColor != null
    } yield cell.asInstanceOf[XSSFCell]).toList


  override def getSheetByIndex(document: Workbook, sheetIndex: Int): Sheet = document.getSheetAt(sheetIndex)

  override def getSheetCount(document: Workbook): Int = document.getNumberOfSheets

  override def loadDocument(path: Path): Try[Workbook] = Try(new XSSFWorkbook(path.toFile))

  override def saveCorrectedSpreadsheet(compareDocument: Workbook, testPath: Path): Try[Path] = Try({
    val fileNameNew: String = GFiles.getNameWithoutExtension(testPath.toString) + CORRECTION_ADD_STRING +
      "." + GFiles.getFileExtension(testPath.toString)

    val savePath: Path = Paths.get(testPath.getParent.toString, fileNameNew)
    if (!savePath.getParent.toFile.exists())
      Files.createDirectories(savePath.getParent)

    val fileOut: FileOutputStream = new FileOutputStream(savePath.toFile)
    compareDocument.write(fileOut)
    fileOut.close()

    savePath
  })

  override def setCellComment(cell: XSSFCell, message: String): Unit = if (message != null || !message.isEmpty) {

    cell.removeCellComment()

    // Create new drawing object
    val factory: CreationHelper = cell.getSheet.getWorkbook.getCreationHelper

    // Create comment space
    val anchor: ClientAnchor = factory.createClientAnchor()
    anchor.setCol1(cell.getColumnIndex + 1)
    anchor.setCol2(cell.getColumnIndex + 3)
    anchor.setRow1(cell.getRowIndex + 1)
    anchor.setRow2(cell.getRowIndex + 3)

    // Insert new comment
    val comment: Comment = cell.getSheet.createDrawingPatriarch().createCellComment(anchor)
    comment.setVisible(true)
    comment.setString(factory.createRichTextString(message))

    // Set comment
    cell.setCellComment(comment)
  }

  override def setCellStyle(cell: XSSFCell, font: Font, success: Boolean): Unit = {
    font.setColor(if (success) green else red)

    val style: CellStyle = cell.getSheet.getWorkbook.createCellStyle()
    style.setFont(font)

    style.setAlignment(cell.getCellStyle.getAlignmentEnum)
    style.setDataFormat(cell.getCellStyle.getDataFormat)

    style.setBorderLeft(BorderStyle.MEDIUM)
    style.setBorderBottom(BorderStyle.MEDIUM)
    style.setBorderRight(BorderStyle.MEDIUM)
    style.setBorderTop(BorderStyle.MEDIUM)

    cell.setCellStyle(style)
  }

}
