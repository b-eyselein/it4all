package model

import org.junit.Test

import org.scalatest.Assertions._
import ScalaNode._

class ScalaNodeParserTest {

  val (a, b, c): (ScalaNode, ScalaNode, ScalaNode) = ('a', 'b', 'c')

  def testParse(expected: ScalaNode, representations: String*) {
    for (toParse <- representations) {
      val parsedOptional = ScalaNodeParser.parse(toParse)

      assert(parsedOptional.isDefined, "expected that parsing of \"" + toParse + "\" succeeds!")

      assert(parsedOptional.get == expected, "expected that parsing of \"" + toParse + "\" is equal to " + expected)
    }
  }

  @Test
  def testNoOperators() = testParse(a, "a", "A")

  @Test
  def testConstants() {
    testParse(TRUE, "true", "1", "TRUE")

    testParse(FALSE, "false", "0", "FALSE")
  }

  @Test
  def testSingleOperators() {
    testParse(a and b, "a and b", "(a and b)")

    testParse(a or b, "a or b", "(a or b)")

    testParse(a nand b, "a nand b", "(a nand b)")

    testParse(a nor b, "a nor b", "(a nor b)")

    testParse(a xor b, "a xor b", "(a xor b)")

    testParse(a equiv b, "a equiv b", "(a equiv b)")

    testParse(a impl b, "a impl b", "(a impl b)")

    assertThrows[IllegalArgumentException] {
      testParse(a, "a test b")
    }
  }

  @Test
  def testDoubleOperators() {
    testParse(a and b or c, "a and b or c")
    testParse(a and (b or c), "a and (b or c)")

    testParse(a and b or c, "(a and b) or c")

    testParse(a or (b and c), "a or b and c")
    testParse(a or (b and c), "a or (b and c)")

    testParse((a or b) and c, "(a or b) and c")
  }

  @Test
  def testTripleOperators() {
    testParse((a and b) or (b and a), "a and b or b and a")
    testParse(a and (b or (b and a)), "a and (b or b and a)")
  }

}