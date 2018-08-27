package model.spread

import java.nio.file.{Path, Paths}

import model.spread.ODFCorrector._
import model.spread.SpreadUtils._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert._
import org.junit.{Assert, Test}
import org.odftoolkit.simple.SpreadsheetDocument
import org.odftoolkit.simple.style.Font
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle
import org.odftoolkit.simple.table.Cell
import org.scalatest.Matchers._

import scala.util.{Failure, Success}

class ODFCorrectorTest {

  private val standardDocument: Path = Paths.get("test/resources/spread/standard.ods")

  private val schullandheimDir: Path = Paths.get("test/resources/spread/schullandheim")

  private val schullandheimMuster     : Path = Paths.get(schullandheimDir.toString, "Aufgabe_Schullandheim_Muster.ods")
  private val schullandheimTeilLoesung: Path = Paths.get(schullandheimDir.toString, "Aufgabe_Schullandheim.ods")


  private def getCell(workbook: SpreadsheetDocument, sheetIndex: Int, rowIndex: Int, cellIndex: Int): Cell =
    workbook.getSheetByIndex(sheetIndex).getCellByPosition(rowIndex, cellIndex)

  @Test def testCompareChartsInSheet() {
    compareChartsInSheet(null, null) shouldBe ((false, ""))
  }

  @Test def testCloseDocument(): Unit = loadDocument(standardDocument) match {
    case Failure(e)        => Assert.fail(e.getMessage)
    case Success(document) =>
      assertNotNull(document)
      closeDocument(document)
  }

  @Test def testCompareCellFormulas(): Unit = {

    val filesTries = for {
      muster <- loadDocument(schullandheimMuster)
      teilLsg <- loadDocument(schullandheimTeilLoesung)
    } yield (muster, teilLsg)

    filesTries match {
      case Failure(e)                 => Assert.fail(e.getMessage)
      case Success((muster, teilLsg)) =>

        compareCellFormulas(getCell(muster, 2, 7, 15), getCell(teilLsg, 2, 7, 15)) shouldBe ((true, formulaCorrect))

        // Wert in Muster null, Compare leer
        compareCellFormulas(getCell(muster, 3, 3, 9), getCell(teilLsg, 3, 3, 9)) shouldBe ((true, noFormulaRequired))

        // Wert in Muster, Compare leer
        compareCellFormulas(getCell(muster, 3, 5, 16), getCell(teilLsg, 3, 5, 16)) shouldBe ((false, formulaMissing))

        // Wert in Muster, Compare leer
        compareCellFormulas(getCell(muster, 3, 5, 19), getCell(teilLsg, 3, 5, 19))
          .shouldBe((false, "Formel falsch. Die Bereiche [D20] fehlen."))
    }
  }

  @Test def testCompareCellValues() {
    //    val muster = loadDocument(schullandheimMuster)
    //    val teilLsg = loadDocument(schullandheimTeilLoesung)

    //    assertThat(compareCellValues(getCell(muster, 2, 7, 15), getCell(teilLsg, 2, 7, 15)),
    //        equalTo(StringConsts.COMMENT_VALUE_CORRECT))

    // Wert in Muster, Compare leer
    //    assertThat(compareCellValues(getCell(muster, 3, 5, 16), getCell(teilLsg, 3, 5, 16)),
    //        equalTo(StringConsts.COMMENT_VALUE_MISSING))

    // Wert in Muster, Compare falsch
    //    assertThat(compareCellValues(getCell(muster, 3, 5, 19), getCell(teilLsg, 3, 5, 19)),
    //        equalTo(String.format(StringConsts.COMMENT_VALUE_INCORRECT_VAR, "12,14 €")))

  }

  @Test def testCompareNumberOfChartsInDocument(): Unit = {
    val fileTries = for {
      muster <- loadDocument(schullandheimMuster)
      solution <- loadDocument(schullandheimTeilLoesung)
    } yield (muster, solution)

    fileTries match {
      case Failure(e)                  => Assert.fail(e.getMessage)
      case Success((muster, solution)) =>
        //    assertThat(compareNumberOfChartsInDocument(muster, muster),
        //        equalTo(StringConsts.COMMENT_CHART_NUM_CORRECT))

        //    assertThat(compareNumberOfChartsInDocument(solution, muster),
        //        equalTo(String.format(StringConsts.COMMENT_CHART_NUM_INCORRECT_VAR, 2, 0)))

        //    assertThat(compareNumberOfChartsInDocument(solution, solution),
        //        equalTo(StringConsts.COMMENT_CHART_FALSE))

        muster.close()
        solution.close()
    }
  }

  @Test def testCompareSheet() {
    // TODO: implement and test!
    // fail("Not yet implemented")
  }

  @Test def testGetCellByPosition(): Unit = loadDocument(schullandheimMuster) match {
    case Failure(e)      => Assert.fail(e.getMessage)
    case Success(muster) =>
      val musterSheet = getSheetByIndex(muster, 0)
      assertThat(getCellByPosition(musterSheet, 0, 0).get.getStringValue, equalTo(musterSheet.getCellByPosition("A1").getStringValue))
      assertThat(getCellByPosition(musterSheet, 7, 5).get.getStringValue, equalTo(musterSheet.getCellByPosition("F8").getStringValue))

      val musterSheet4 = getSheetByIndex(muster, 4)
      assertThat(getCellByPosition(musterSheet4, 10, 15).get.getStringValue, equalTo(musterSheet4.getCellByPosition(10, 15).getStringValue))
      muster.close()
  }

  @Test def testGetColoredRange(): Unit = loadDocument(schullandheimMuster) match {
    case Failure(e)      => Assert.fail(e.getMessage)
    case Success(muster) =>
      assertTrue(getColoredRange(muster.getSheetByIndex(1)).isEmpty)

      val coloredRange = getColoredRange(muster.getSheetByIndex(2))
      coloredRange.size shouldBe 28

      for (i <- 0 until 25) {
        val cell = coloredRange.apply(i)
        cell.getRowIndex shouldBe 15 + i

        cell.getColumnIndex shouldBe 7
      }
      val maennlich = coloredRange.apply(26)
      maennlich.getRowIndex shouldBe 44
      maennlich.getColumnIndex shouldBe 1

      val weiblich = coloredRange.apply(27)
      weiblich.getRowIndex shouldBe 45
      weiblich.getColumnIndex shouldBe 1
  }

  @Test def testGetSheetByIndex() {
    val document = loadDocument(standardDocument).get
    val sheet = getSheetByIndex(document, 0)
    assertNotNull(sheet)
    sheet.getOwnerDocument shouldBe document
    document.close()

    val muster = loadDocument(schullandheimMuster).get

    val musterSheet = getSheetByIndex(muster, 0)
    assertNotNull(musterSheet)
    musterSheet.getOwnerDocument shouldBe muster
    musterSheet.getCellByPosition("A1").getStringValue shouldBe "Verwalten und Auswerten von Daten"

    val musterSheet2 = getSheetByIndex(muster, 4)
    assertNotNull(musterSheet2)
    musterSheet2.getOwnerDocument shouldBe muster
    musterSheet2.getCellByPosition("K16").getStringValue shouldBe "5% Rabatt auf den Gesamtpreis"
    muster.close()
  }

  @Test def testGetSheetCount() {
    val document = loadDocument(standardDocument).get
    getSheetCount(document) shouldBe 1
    document.close()

    val document2 = loadDocument(schullandheimMuster).get
    getSheetCount(document2) shouldBe 8
    document2.close()

    val document3 = loadDocument(schullandheimTeilLoesung).get
    getSheetCount(document3) shouldBe 8
    document3.close()
  }

  @Test def testLoadDocument() {
    assertNotNull(loadDocument(standardDocument).get)

    assertNotNull(loadDocument(schullandheimMuster).get)
  }

  @Test
  def testLoadDocumentWithWrongPath() {
    assertTrue(loadDocument(Paths.get("")).isFailure)
  }

  @Test def testSaveCorrectedSpreadsheet() {
    // TODO: implement and test!
    // fail("Not yet implemented")
  }

  @Test def testSetCellComment() {
    val message = "Dies ist eine Testnachricht!"
    val message2 = "Dies ist eine zweite  Testnachricht!"

    val document = loadDocument(standardDocument).get
    val cell = document.getSheetByIndex(0).getCellByPosition("D4")

    assertNull(cell.getNoteText)

    setCellComment(cell, null)
    assertNull(cell.getNoteText)

    setCellComment(cell, "")
    assertNull(cell.getNoteText)

    setCellComment(cell, message)
    cell.getNoteText shouldBe message

    setCellComment(cell, message2)
    cell.getNoteText shouldBe message2

    cell.setNoteText(null)

  }

  @Test def testSetCellStyle(): Unit = loadDocument(standardDocument) match {
    case Failure(e)        => Assert.fail(e.getMessage)
    case Success(document) =>
      val cell = document.getSheetByIndex(0).getCellByPosition("B2")
      val font = new Font("Arial", FontStyle.BOLD, 11)
      assertNotNull(cell.getFont)
      setCellStyle(cell, font, /* Color.AQUA*/ success = false)
      cell.getFont shouldBe font
      cell.setCellStyleName(null)
  }

}
