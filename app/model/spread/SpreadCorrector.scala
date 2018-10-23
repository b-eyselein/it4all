package model.spread

import java.nio.file.Path

import model.core.FileUtils
import model.core.result.{EvaluationResult, SuccessType}
import model.spread.SpreadConsts._

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

// Types of results

abstract sealed class SpreadSheetCorrectionResult(val success: SuccessType, val notices: Seq[String]) extends EvaluationResult

final case class SpreadSheetCorrectionFailure(cause: String) extends SpreadSheetCorrectionResult(SuccessType.ERROR, Seq(cause))

final case class SpreadSheetCorrectionError(strs: Seq[String]) extends SpreadSheetCorrectionResult(SuccessType.NONE, strs)

case object SpreadSheetCorrectionSuccess extends SpreadSheetCorrectionResult(SuccessType.COMPLETE, Seq(SUCCESS_CORRECTION))

// Actual corrector

abstract class SpreadCorrector extends FileUtils {

  // Abstract types

  protected type DocumentType

  protected type SheetType

  protected type CellType

  protected type FontType

  protected type ColorType

  // Colors

  protected val green: ColorType

  protected val red: ColorType


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
              // write message in RobotCell A0 on first Sheet
              getCellByPosition(getSheetByIndex(compareDocument, 0), 0, 0) match {
                case None       => Unit
                case Some(cell) => setCellComment(cell, chartCompMessage)
              }
            }

            val notices: ListBuffer[String] = ListBuffer.empty

            // Iterate over sheets
            val sheetCount = getSheetCount(sampleDocument)
            for (sheetIndex <- 0 until sheetCount) {

              val sampleTable: SheetType = getSheetByIndex(sampleDocument, sheetIndex)
              val compareTable: SheetType = getSheetByIndex(compareDocument, sheetIndex)

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


  protected def compareCellFormulas(masterCell: CellType, compareCell: CellType): (Boolean, String)

  protected def compareCellValues(masterCell: CellType, compareCell: CellType): (Boolean, String)

  protected def compareChartsInSheet(compareSheet: SheetType, sampleSheet: SheetType): (Boolean, String)

  protected def compareNumberOfChartsInDocument(compareDocument: DocumentType, sampleDocument: DocumentType): (Boolean, String)

  protected def compareSheet(sampleTable: SheetType, compareTable: SheetType, conditionalFormating: Boolean): Unit

  protected def compareSheetConditionalFormatting(master: SheetType, compare: SheetType): Seq[String]

  // Getters and other helper methods

  protected def getCellByPosition(table: SheetType, row: Int, column: Int): Option[CellType]

  protected def getColoredRange(master: SheetType): Seq[CellType]

  protected def getSheetByIndex(sampleDocument: DocumentType, sheetIndex: Int): SheetType

  protected def getSheetCount(sampleDocument: DocumentType): Int

  protected def closeDocument(compareDocument: DocumentType): Unit

  protected def loadDocument(musterPath: Path): Try[DocumentType]

  protected def saveCorrectedSpreadsheet(compareDocument: DocumentType, testPath: Path): Try[Path]

  protected def setCellComment(cell: CellType, comment: String): Unit

  protected def setCellStyle(cell: CellType, font: FontType, success: Boolean): Unit

}
