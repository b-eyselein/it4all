package model

import java.nio.file.{Files, Path, Paths}

import model.CommonUtils.RicherTry
import model.StringConsts._
import model.XLSXCorrectorTest._
import org.apache.poi.ss.usermodel.{Cell, Sheet, Workbook}
import org.apache.poi.xssf.usermodel.XSSFCell
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert._
import org.junit._
import org.scalatest.Matchers._

import scala.util.{Failure, Success}

object XLSXCorrectorTest {

  println(System.getProperty("user.dir"))

  val testDir: Path = Paths.get("test", "resources", "spreadsheet")

  val stdDoc: Path = Paths.get(testDir.toString, "standard.xlsx")

  val testMuster: Path = Paths.get(testDir.toString, "schullandheim", "Aufgabe_Schullandheim_Muster.xlsx")
  val testSolution: Path = Paths.get(testDir.toString, "schullandheim", "Aufgabe_Schullandheim.xlsx")

  val testMusterCopy: Path = Paths.get(testDir.toString, "schullandheim", "testMusterCopy.xlsx")
  val testSolutionCopy: Path = Paths.get(testDir.toString, "schullandheim", "testSolutionCopy.xlsx")

  @BeforeClass
  def setUp(): Unit = {
    Files.copy(testMuster, testMusterCopy)
    Files.copy(testSolution, testSolutionCopy)
  }

  @AfterClass
  def tearDown(): Unit = {
    Files.delete(testMusterCopy)
    Files.delete(testSolutionCopy)
  }

}

class XLSXCorrectorTest {

  private def failureLoad(path: Path, e: Throwable): String = s"Could not load workbook $path:"

  @Test
  def testCloseDocument(): Unit = XLSXCorrector.loadDocument(testMusterCopy) match {
    case Failure(e) => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(document) => XLSXCorrector.closeDocument(document)
  }

  private def getCell(workbook: Workbook, sheetIndex: Int, rowIndex: Int, cellIndex: Int) =
    workbook.getSheetAt(sheetIndex).getRow(rowIndex).getCell(cellIndex).asInstanceOf[XSSFCell]

  @Test
  def testCompareCellFormulas(): Unit = XLSXCorrector.loadDocument(testMusterCopy).zip(XLSXCorrector.loadDocument(testSolution)) match {
    case Failure(e) => Assert.fail(failureLoad(null, e))
    case Success((muster, teilLsg)) =>
      XLSXCorrector.compareCellFormulas(getCell(muster, 2, 15, 7), getCell(teilLsg, 2, 15, 7)) shouldBe(true, COMMENT_FORMULA_CORRECT)

      // Kein Formel in Muster, Compare leer
      XLSXCorrector.compareCellFormulas(getCell(muster, 3, 16, 1), getCell(teilLsg, 3, 16, 1)) shouldBe(false, COMMENT_FORMULA_FALSE)

      // Wert in Muster, Compare leer
      XLSXCorrector.compareCellFormulas(getCell(muster, 3, 16, 5), getCell(teilLsg, 3, 16, 5)) shouldBe(false, COMMENT_FORMULA_MISSING)

      // Wert in Muster, Compare leer
      XLSXCorrector.compareCellFormulas(getCell(muster, 3, 19, 5), getCell(teilLsg, 3, 19, 5)) shouldBe(false, "Formel falsch. Die Bereiche [D20] fehlen.")
  }

  @Test
  def testCompareCellValues(): Unit = XLSXCorrector.loadDocument(testMusterCopy).zip(XLSXCorrector.loadDocument(testSolution)) match {
    case Failure(e) => Assert.fail(failureLoad(null, e))
    case Success((muster, teilLsg)) =>

      XLSXCorrector.compareCellValues(getCell(muster, 2, 15, 7), getCell(teilLsg, 2, 15, 7)) shouldBe(true, COMMENT_VALUE_CORRECT)

      // Wert in Muster, Compare leer
      XLSXCorrector.compareCellValues(getCell(muster, 3, 16, 5), getCell(teilLsg, 3, 16, 5)) shouldBe(false, COMMENT_VALUE_MISSING)

      // Wert in Muster, Compare falsch
      // TODO: Zahl schoener formatieren... 12.142857142857142
      XLSXCorrector.compareCellValues(getCell(muster, 3, 19, 5), getCell(teilLsg, 3, 19, 5)) shouldBe(false, "Wert falsch. Erwartet wurde '12.142857142857142'.")
  }

  @Test
  def testCompareChartsInSheet(): Unit = XLSXCorrector.loadDocument(testMusterCopy).zip(XLSXCorrector.loadDocument(testSolution)) match {
    case Failure(e) => Assert.fail(failureLoad(null, e))
    case Success((muster, teilLsg)) =>
      XLSXCorrector.compareChartsInSheet(muster.getSheetAt(0), muster.getSheetAt(0)) shouldBe(false, COMMENT_CHART_FALSE)

      XLSXCorrector.compareChartsInSheet(teilLsg.getSheetAt(0), muster.getSheetAt(2)) shouldBe(false, "Falsche Anzahl Diagramme im Dokument (Erwartet: 2, Gefunden: 0).")

      XLSXCorrector.compareChartsInSheet(muster.getSheetAt(2), muster.getSheetAt(2)) shouldBe(true, COMMENT_CHART_CORRECT)
    // TODO: Implement!
    // Assert.fail("Still things to implement!")
  }

  @Test
  def testCompareNumberOfChartsInDocument(): Unit = XLSXCorrector.loadDocument(stdDoc) match {
    case Failure(e) => Assert.fail(failureLoad(stdDoc, e))
    case Success(document) => XLSXCorrector.compareNumberOfChartsInDocument(document, document) shouldBe(true, "")
  }

  @Test
  def testCompareSheet(): Unit = {
    // TODO: implement and test!
    // Assert.fail("Not yet implemented")
  }

  @Test
  def testGetCellByPosition(): Unit = XLSXCorrector.loadDocument(testMusterCopy) match {
    case Failure(e) => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(muster) =>
      val musterSheet: Sheet = XLSXCorrector.getSheetByIndex(muster, 0)
      assertThat(XLSXCorrector.getCellByPosition(musterSheet, 0, 0).toString, equalTo(musterSheet.getRow(0).getCell(0).toString))

      assertNull(XLSXCorrector.getCellByPosition(musterSheet, 7, 5))

      val musterSheet4: Sheet = XLSXCorrector.getSheetByIndex(muster, 4)
      assertThat(XLSXCorrector.getCellByPosition(musterSheet4, 15, 10).toString, equalTo(musterSheet4.getRow(15).getCell(10).toString))

  }

  @Test
  def testGetColoredRange(): Unit = XLSXCorrector.loadDocument(testMusterCopy) match {
    case Failure(e) => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(muster) =>
      assertTrue(XLSXCorrector.getColoredRange(muster.getSheetAt(1)).isEmpty)

      val coloredRange = XLSXCorrector.getColoredRange(muster.getSheetAt(2))
      coloredRange.size shouldBe (28)
      for (i <- 0 until 25) {
        val cell: Cell = coloredRange(i)
        cell.getRowIndex shouldBe (15 + i)
        cell.getColumnIndex shouldBe (7)
      }

      val maennlich: Cell = coloredRange(26)
      maennlich.getRowIndex shouldBe (44)
      maennlich.getColumnIndex shouldBe (1)

      val weiblich: Cell = coloredRange(27)
      weiblich.getRowIndex shouldBe (45)
      weiblich.getColumnIndex shouldBe (1)
  }

  @Test
  def testGetSheetByIndex(): Unit = {
    XLSXCorrector.loadDocument(stdDoc) match {
      case Failure(e) => Assert.fail(failureLoad(stdDoc, e))
      case Success(document) =>
        assertNotNull("Standarddokument konnte nicht geladen werden!", document)
        assertNotNull(XLSXCorrector.getSheetByIndex(document, 0))
    }

    XLSXCorrector.loadDocument(testMusterCopy) match {
      case Failure(e) => Assert.fail(failureLoad(testMusterCopy, e))
      case Success(muster) =>
        val musterSheet1: Sheet = XLSXCorrector.getSheetByIndex(muster, 0)
        assertNotNull(musterSheet1)
        musterSheet1.getRow(0).getCell(0).toString shouldBe ("Verwalten und Auswerten von Daten")

        val musterSheet2 = XLSXCorrector.getSheetByIndex(muster, 4)
        assertNotNull(musterSheet2)
        musterSheet2.getRow(15).getCell(10).toString shouldBe ("5% Rabatt auf den Gesamtpreis")
    }
  }

  @Test
  def testGetSheetCount(): Unit = {
    XLSXCorrector.loadDocument(stdDoc) match {
      case Failure(e) => Assert.fail(failureLoad(stdDoc, e))
      case Success(document) =>
        assertNotNull("Standarddokument konnte nicht geladen werden!", document)
        XLSXCorrector.getSheetCount(document) shouldBe (1)
    }

    XLSXCorrector.loadDocument(testMusterCopy) match {
      case Failure(e) => Assert.fail(failureLoad(testMusterCopy, e))
      case Success(document) =>
        XLSXCorrector.getSheetCount(document) shouldBe (8)
    }

    XLSXCorrector.loadDocument(testSolutionCopy) match {
      case Failure(e) => Assert.fail(failureLoad(testSolutionCopy, e))
      case Success(document) =>
        XLSXCorrector.getSheetCount(document) shouldBe (8)
    }
  }

  @Test
  def testLoadDocument(): Unit = {
    assertTrue("Dokument standardDokument konnte nicht geladen werden!", XLSXCorrector.loadDocument(stdDoc).isSuccess)

    assertTrue("Dokument schullandheimMuster konnte nicht geladen werden!", XLSXCorrector.loadDocument(testMusterCopy).isSuccess)

    assertTrue("Dokument schullandheimMuster konnte nicht geladen werden!", XLSXCorrector.loadDocument(testSolutionCopy).isSuccess)
  }


  def testLoadDocumentWithWrongPath(): Unit = assertTrue(XLSXCorrector.loadDocument(Paths.get("")).isFailure)

  @Test
  def testSaveCorrectedSpreadsheet() {
    // TODO: implement and test!
    // Assert.fail("Not yet implemented")
  }

  @Test
  def testSetCellComment(): Unit = XLSXCorrector.loadDocument(stdDoc) match {
    case Failure(e) => Assert.fail(failureLoad(stdDoc, e))
    case Success(document) =>
      val (message, message2) = ("Dies ist eine Testnachricht!", "Dies ist eine zweite  Testnachricht!")

      val cell: XSSFCell = getCell(document, 0, 0, 0)

      assertNull(cell.getCellComment)

      XLSXCorrector.setCellComment(cell, null)
      assertNull(cell.getCellComment)

      XLSXCorrector.setCellComment(cell, "")
      assertNull(cell.getCellComment)

      XLSXCorrector.setCellComment(cell, message)
      cell.getCellComment.getString.getString shouldBe (message)

      XLSXCorrector.setCellComment(cell, message2)
      cell.getCellComment.getString.getString shouldBe (message2)

      cell.removeCellComment()
  }
}
