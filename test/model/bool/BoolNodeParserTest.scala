package model.bool

import org.junit.Test
import org.scalatest.Assertions._

import scala.util.{Failure, Success}

class BoolNodeParserTest {

  val (a, b, c): (ScalaNode, ScalaNode, ScalaNode) = (Variable('a'), Variable('b'), Variable('c'))

  def testParse(expected: ScalaNode, representations: String*): Unit = for (toParse <- representations) {
    BoolNodeParser.parseBoolFormula(toParse) match {
      case Success(parsed) => assert(parsed == expected, s"""expected that parsing "$toParse" is equal to $expected""")
      case Failure(error)  => fail(s"""expected that parsing of "$toParse" succeeds, but got error $error!""")
    }
  }

  @Test
  def testNoOperators(): Unit = {
    testParse(a, "a", "A")
  }

  @Test
  def testConstants(): Unit = {
    testParse(TRUE, "true", "1", "TRUE")

    testParse(FALSE, "false", "0", "FALSE")
  }

  @Test
  def testSingleOperators(): Unit = {
    testParse(a and b, "a and b", "(a and b)")

    testParse(a or b, "a or b", "(a or b)")

    testParse(a nand b, "a nand b", "(a nand b)")

    testParse(a nor b, "a nor b", "(a nor b)")

    testParse(a xor b, "a xor b", "(a xor b)")

    testParse(a equiv b, "a equiv b", "(a equiv b)")

    testParse(a impl b, "a impl b", "(a impl b)")

    BoolNodeParser.parseBoolFormula("a test b") match {
      case Success(formula) => fail("Parsing of 'a test b' should fail, but succeeded with " + formula.toString)
      case _                => Unit
    }

  }

  @Test
  def testDoubleOperators(): Unit = {
    testParse(a and b or c, "a and b or c")
    testParse(a and (b or c), "a and (b or c)")

    testParse(a and b or c, "(a and b) or c")

    testParse(a or (b and c), "a or b and c")
    testParse(a or (b and c), "a or (b and c)")

    testParse((a or b) and c, "(a or b) and c")
  }

  @Test
  def testTripleOperators(): Unit = {
    testParse((a and b) or (b and a), "a and b or b and a")
    testParse(a and (b or (b and a)), "a and (b or b and a)")
  }

}