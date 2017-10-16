package model

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.{assertFalse, assertThat, assertTrue}
import org.junit.Test

class SpreadUtilsTest {
  val firstCFFormula = "<main:formula>$G$2/$G$9</main:formula></main:cfRule></xml-fragment>"
  val secondCFFormula = "<main:formula>($H$2:$H$9)</main:formula></main:cfRule></xml-fragment>"

  @Test
  def newTest(): Unit = {
    val string1 = SpreadUtils.getExcelCFFormulaList(firstCFFormula)
    val string2 = SpreadUtils.getExcelCFFormulaList(secondCFFormula)
    SpreadUtils.getDiffOfTwoFormulas(string1, string2).shouldBe("Ein Operator [/] fehlt. Der Bereich [G9, G2] fehlt.")
  }

  @Test
  def testGetCellFormulasList(): Unit = {
    var list = SpreadUtils.getCellFormulasList()
  }

  @Test
  def testGetDifferenceOfCollections(): Unit = {
    val strings1 = List("Hallo", "dies", "ist", "ein", "Test").toSet
    val strings2 = List("Hallo", "das", "ist", "ein", "schlechter", "Test").toSet

    val diff = SpreadUtils.getDifferenceOfCollections(strings1, strings2)
    val iter = diff.iterator()
    assertTrue(iter.hasNext())
    assertThat(iter.next(), equalTo("dies"))
    assertFalse(iter.hasNext())

    diff = SpreadUtils.getDifferenceOfCollections(strings2, strings1)
    iter = diff.iterator()
    assertTrue(iter.hasNext())
    assertThat(iter.next(), equalTo("schlechter"))
    assertTrue(iter.hasNext())
    assertThat(iter.next(), equalTo("das"))
    assertFalse(iter.hasNext())
  }

  @Test
  def testGetSheetCFDiff(): Unit = {
    // fail("Not yet implemented")
  }
}
