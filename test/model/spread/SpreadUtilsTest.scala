package model.spread

import model.spread.SpreadUtils._
import org.junit.Test
import org.scalatest.Matchers._

class SpreadUtilsTest {

  // Strings

  private[this] val gib = "giBbERisH"

  private[this] val titles = List("", "Test1", "MYTEST2", " This is the third test...")

  private[this] val formulas = Set("SUM", "TODAY", "IF")

  private[this] val operators = Set("+", "-", "*", "/")

  private[this] val ranges = Set("A1", "B5", "AZ39", "AF477")

  @Test
  def testRenderDiffAsString(): Unit = {
    renderDiffAsStr("Bereiche", Seq[String]()) shouldBe ""
    renderDiffAsStr("Bereiche", Seq("A1")) shouldBe "Die Bereiche [A1] fehlen."
    renderDiffAsStr("Operatoren", Seq("+", "-")) shouldBe "Die Operatoren [+, -] fehlen."

  }

  @Test
  def testGetExcelChartTitle(): Unit = for (title <- titles) {
    getExcelChartTitle(s"<a:t>$title</a:t>") shouldBe Some(title)
    getExcelChartTitle(title) shouldBe None
  }

  @Test
  def testGetCellFormulas(): Unit = {
    for (formula <- formulas) {
      getCellFormulas(formula + "(") shouldBe Set(formula)
      getCellFormulas(formula) shouldBe Set.empty
    }

    getCellFormulas(formulas.mkString("(") + "(") shouldBe formulas
  }

  @Test
  def testGetCellOperators(): Unit = {
    for (op <- operators) {
      getCellOperators(op) shouldBe Set(op)
      getCellOperators(gib + op + gib) shouldBe Set(op)
    }

    getCellOperators(gib + operators.mkString(gib) + gib) shouldBe operators
  }

  @Test
  def testGetCellRanges(): Unit = {
    for (r <- ranges)
      getCellRanges(r) shouldBe Set(r)

    getCellRanges(ranges.mkString) shouldBe ranges
  }

  @Test
  def testGetExcelChartRanges(): Unit = {
    //    "<c:f>$name!(.*?)</c:f>"
  }

}
