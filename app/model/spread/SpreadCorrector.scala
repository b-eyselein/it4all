package model.spread

import java.nio.file.Path

import model.spread.SpreadConsts._

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

// Types of results

abstract sealed class SpreadSheetCorrectionResult(val success: Boolean, val notices: List[String])

case class SpreadSheetCorrectionFailure(cause: String) extends SpreadSheetCorrectionResult(false, List(cause))

case class SpreadSheetCorrectionError(strs: List[String]) extends SpreadSheetCorrectionResult(false, strs)

case object SpreadSheetCorrectionSuccess extends SpreadSheetCorrectionResult(true, List(SUCCESS_CORRECTION))

// Actual corrector

abstract class SpreadCorrector[D, S, C, F, ColorType](val green: ColorType, val red: ColorType) {

  def correct(samplePath: Path, comparePath: Path, conditionalFormating: Boolean, compareCharts: Boolean): SpreadSheetCorrectionResult = {
    if (!samplePath.toFile.exists) SpreadSheetCorrectionFailure(ERROR_MISSING_SAMPLE)

    else if (!comparePath.toFile.exists) SpreadSheetCorrectionFailure(ERROR_MISSING_SOLUTION)

    else loadDocument(samplePath) match {
      case Failure(_) => SpreadSheetCorrectionFailure(ERROR_LOAD_SAMPLE)

      case Success(sampleDocument) => loadDocument(comparePath) match {

        case Failure(_) => SpreadSheetCorrectionFailure(ERROR_LOAD_SOLUTION)

        case Success(compareDocument) =>
          if (getSheetCount(sampleDocument) != getSheetCount(compareDocument))
            SpreadSheetCorrectionFailure(ERROR_WRONG_SHEET_NUM)
          else {
            if (compareCharts) {
              val (_, chartCompMessage) = compareNumberOfChartsInDocument(compareDocument, sampleDocument)
              // write message in Cell A0 on first Sheet
              getCellByPosition(getSheetByIndex(compareDocument, 0), 0, 0) match {
                case None       => Unit
                case Some(cell) => setCellComment(cell, chartCompMessage)
              }
            }

            val notices: ListBuffer[String] = ListBuffer.empty

            // Iterate over sheets
            val sheetCount = getSheetCount(sampleDocument)
            for (sheetIndex <- 0 until sheetCount) {

              val sampleTable: S = getSheetByIndex(sampleDocument, sheetIndex)
              val compareTable: S = getSheetByIndex(compareDocument, sheetIndex)

              if (compareTable == null || sampleTable == null)
                notices += s"""Beim Öffnen der ${sheetCount + 1}. Tabelle ist ein Fehler aufgetreten."""
              else
                compareSheet(sampleTable, compareTable, conditionalFormating)
            }

            // Save and close workbooks
            saveCorrectedSpreadsheet(compareDocument, comparePath)
            closeDocument(compareDocument)
            closeDocument(sampleDocument)

            if (notices.isEmpty) SpreadSheetCorrectionSuccess
            else SpreadSheetCorrectionError(notices.toList)
          }
      }
    }
  }


  protected def compareCellFormulas(masterCell: C, compareCell: C): (Boolean, String)

  protected def compareCellValues(masterCell: C, compareCell: C): (Boolean, String)

  protected def compareChartsInSheet(compareSheet: S, sampleSheet: S): (Boolean, String)

  protected def compareNumberOfChartsInDocument(compareDocument: D, sampleDocument: D): (Boolean, String)

  protected def compareSheet(sampleTable: S, compareTable: S, conditionalFormating: Boolean): Unit

  protected def compareSheetConditionalFormatting(master: S, compare: S): List[String]

  // Getters and other helper methods

  protected def getCellByPosition(table: S, row: Int, column: Int): Option[C]

  protected def getColoredRange(master: S): List[C]

  protected def getSheetByIndex(sampleDocument: D, sheetIndex: Int): S

  protected def getSheetCount(sampleDocument: D): Int

  protected def closeDocument(compareDocument: D): Unit

  protected def loadDocument(musterPath: Path): Try[D]

  protected def saveCorrectedSpreadsheet(compareDocument: D, testPath: Path): Try[Path]

  protected def setCellComment(cell: C, comment: String): Unit

  protected def setCellStyle(cell: C, font: F, success: Boolean): Unit

}
