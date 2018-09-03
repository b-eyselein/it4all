package model.spread

import java.nio.file.{Files, Path, Paths}

import model.spread.SpreadConsts._
import model.spread.SpreadUtils._
import model.spread.XLSXCorrector._
import model.spread.XLSXCorrectorTest._
import org.apache.poi.ss.usermodel.{Cell, Sheet, Workbook}
import org.apache.poi.xssf.usermodel.XSSFCell
import org.junit.Assert._
import org.junit._
import org.scalatest.Matchers._

import scala.util.{Failure, Success}

object XLSXCorrectorTest {

  val testDir: Path = Paths.get("test", "resources", "spread")

  val stdDoc: Path = Paths.get(testDir.toString, "standard.xlsx")

  val testMuster  : Path = Paths.get(testDir.toString, "schullandheim", "Aufgabe_Schullandheim_Muster.xlsx")
  val testSolution: Path = Paths.get(testDir.toString, "schullandheim", "Aufgabe_Schullandheim.xlsx")

  val testMusterCopy  : Path = Paths.get(testDir.toString, "schullandheim", "testMusterCopy.xlsx")
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

  // @Test
  def testCloseDocument(): Unit = loadDocument(testMusterCopy) match {
    case Failure(e)        => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(document) => closeDocument(document)
  }

  private def getCell(workbook: Workbook, sheetIndex: Int, rowIndex: Int, cellIndex: Int) =
    workbook.getSheetAt(sheetIndex).getRow(rowIndex).getCell(cellIndex).asInstanceOf[XSSFCell]

  // @Test
  def testCompareCellFormulas(): Unit = {
    val fileTries = for {
      muster <- loadDocument(testMusterCopy)
      teilLsg <- loadDocument(testSolution)
    } yield (muster, teilLsg)

    fileTries match {
      case Failure(e)                 => Assert.fail(failureLoad(null, e))
      case Success((muster, teilLsg)) =>
        compareCellFormulas(getCell(muster, 2, 15, 7), getCell(teilLsg, 2, 15, 7)) shouldBe ((true, formulaCorrect))

        // Kein Formel in Muster, Compare leer
        compareCellFormulas(getCell(muster, 3, 16, 1), getCell(teilLsg, 3, 16, 1)) shouldBe ((false, noFormulaRequired))

        // Wert in Muster, Compare leer
        compareCellFormulas(getCell(muster, 3, 16, 5), getCell(teilLsg, 3, 16, 5)) shouldBe ((false, formulaMissing))

        // Wert in Muster, Compare leer
        compareCellFormulas(getCell(muster, 3, 19, 5), getCell(teilLsg, 3, 19, 5))
          .shouldBe((false, "Formel falsch. Die Bereiche [D20] fehlen."))
    }
  }

  // @Test
  def testCompareCellValues(): Unit = {
    val fileTries = for {
      muster <- loadDocument(testMusterCopy)
      solution <- loadDocument(testSolution)
    } yield (muster, solution)

    fileTries match {
      case Failure(e)                 => Assert.fail(failureLoad(null, e))
      case Success((muster, teilLsg)) =>

        compareCellValues(getCell(muster, 2, 15, 7), getCell(teilLsg, 2, 15, 7)) shouldBe ((true, COMMENT_VALUE_CORRECT))

        // Wert in Muster, Compare leer
        compareCellValues(getCell(muster, 3, 16, 5), getCell(teilLsg, 3, 16, 5)) shouldBe ((false, COMMENT_VALUE_MISSING))

        // Wert in Muster, Compare falsch
        // TODO: Zahl schoener formatieren... 12.142857142857142
        compareCellValues(getCell(muster, 3, 19, 5), getCell(teilLsg, 3, 19, 5))
          .shouldBe((false, "Wert falsch. Erwartet wurde '12.142857142857142'."))
    }
  }

  // @Test
  def testCompareChartsInSheet(): Unit = {
    val fileTries = for {
      muster <- loadDocument(testMusterCopy)
      solution <- loadDocument(testSolution)
    } yield (muster, solution)

    fileTries match {
      case Failure(e)                 => Assert.fail(failureLoad(null, e))
      case Success((muster, teilLsg)) =>
        compareChartsInSheet(muster.getSheetAt(0), muster.getSheetAt(0)) shouldBe ((false, COMMENT_CHART_FALSE))

        compareChartsInSheet(teilLsg.getSheetAt(0), muster.getSheetAt(2)) shouldBe ((false, "Falsche Anzahl Diagramme im Dokument (Erwartet: 2, Gefunden: 0)."))

        compareChartsInSheet(muster.getSheetAt(2), muster.getSheetAt(2)) shouldBe ((true, COMMENT_CHART_CORRECT))
      // TODO: Implement!
      // Assert.fail("Still things to implement!")
    }
  }

  // @Test
  def testCompareNumberOfChartsInDocument(): Unit = loadDocument(stdDoc) match {
    case Failure(e)        => Assert.fail(failureLoad(stdDoc, e))
    case Success(document) => compareNumberOfChartsInDocument(document, document) shouldBe ((true, ""))
  }

  // @Test
  def testCompareSheet(): Unit = {
    // TODO: implement and test!
    // Assert.fail("Not yet implemented")
  }

  // @Test
  def testGetCellByPosition(): Unit = loadDocument(testMusterCopy) match {
    case Failure(e)      => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(muster) =>
      val musterSheet: Sheet = getSheetByIndex(muster, 0)

      getCellByPosition(musterSheet, 0, 0) match {
        case None       => Assert.fail("TODO!")
        case Some(cell) => cell.toString shouldBe "Verwalten und Auswerten von Daten"
      }

      getCellByPosition(musterSheet, 7, 5) shouldBe None

      val musterSheet4: Sheet = getSheetByIndex(muster, 4)
      getCellByPosition(musterSheet4, 15, 10) match {
        case None       => Assert.fail("")
        case Some(cell) => cell.toString shouldBe "5% Rabatt auf den Gesamtpreis"
      }
  }

  // @Test
  def testGetColoredRange(): Unit = loadDocument(testMusterCopy) match {
    case Failure(e)      => Assert.fail(failureLoad(testMusterCopy, e))
    case Success(muster) =>
      assertTrue(getColoredRange(muster.getSheetAt(1)).isEmpty)

      val coloredRange = getColoredRange(muster.getSheetAt(2))
      coloredRange.size shouldBe 28
      for (i <- 0 until 25) {
        val cell: Cell = coloredRange(i)
        cell.getRowIndex shouldBe (15 + i)
        cell.getColumnIndex shouldBe 7
      }

      val maennlich: Cell = coloredRange(26)
      maennlich.getRowIndex shouldBe 44
      maennlich.getColumnIndex shouldBe 1

      val weiblich: Cell = coloredRange(27)
      weiblich.getRowIndex shouldBe 45
      weiblich.getColumnIndex shouldBe 1
  }

  // @Test
  def testGetSheetByIndex(): Unit = {
    loadDocument(stdDoc) match {
      case Failure(e)        => Assert.fail(failureLoad(stdDoc, e))
      case Success(document) =>
        assertNotNull("Standarddokument konnte nicht geladen werden!", document)
        assertNotNull(getSheetByIndex(document, 0))
    }

    loadDocument(testMusterCopy) match {
      case Failure(e)      => Assert.fail(failureLoad(testMusterCopy, e))
      case Success(muster) =>
        val musterSheet1: Sheet = getSheetByIndex(muster, 0)
        assertNotNull(musterSheet1)
        musterSheet1.getRow(0).getCell(0).toString shouldBe "Verwalten und Auswerten von Daten"

        val musterSheet2 = getSheetByIndex(muster, 4)
        assertNotNull(musterSheet2)
        musterSheet2.getRow(15).getCell(10).toString shouldBe "5% Rabatt auf den Gesamtpreis"
    }
  }

  // @Test
  def testGetSheetCount(): Unit = {
    loadDocument(stdDoc) match {
      case Failure(e)        => Assert.fail(failureLoad(stdDoc, e))
      case Success(document) =>
        assertNotNull("Standarddokument konnte nicht geladen werden!", document)
        getSheetCount(document) shouldBe 1
    }

    loadDocument(testMusterCopy) match {
      case Failure(e)        => Assert.fail(failureLoad(testMusterCopy, e))
      case Success(document) => getSheetCount(document) shouldBe 8
    }

    loadDocument(testSolutionCopy) match {
      case Failure(e)        => Assert.fail(failureLoad(testSolutionCopy, e))
      case Success(document) => getSheetCount(document) shouldBe 8
    }
  }

  // @Test
  def testLoadDocument(): Unit = for (doc <- List(stdDoc, testMusterCopy, testSolutionCopy)) {
    assertTrue(s"Dokument ${doc.toString} konnte nicht geladen werden!", loadDocument(doc).isSuccess)
  }


  def testLoadDocumentWithWrongPath(): Unit = assertTrue(loadDocument(Paths.get("")).isFailure)

  // @Test
  def testSaveCorrectedSpreadsheet() {
    // TODO: implement and test!
    // Assert.fail("Not yet implemented")
  }

  // @Test
  def testSetCellComment(): Unit = loadDocument(stdDoc) match {
    case Failure(e)        => Assert.fail(failureLoad(stdDoc, e))
    case Success(document) =>
      val (message, message2) = ("Dies ist eine Testnachricht!", "Dies ist eine zweite  Testnachricht!")

      val cell: XSSFCell = getCell(document, 0, 0, 0)

      assertNull(cell.getCellComment)

      setCellComment(cell, null)
      assertNull(cell.getCellComment)

      setCellComment(cell, "")
      assertNull(cell.getCellComment)

      setCellComment(cell, message)
      cell.getCellComment.getString.getString shouldBe message

      setCellComment(cell, message2)
      cell.getCellComment.getString.getString shouldBe message2

      cell.removeCellComment()
  }
}
