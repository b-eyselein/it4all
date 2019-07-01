package model.tools.bool

import org.junit.Test
import org.scalatest.Assertions._

import scala.util.{Failure, Success}

class BoolNodeParserTest {

  val (aVar, bVar, cVar): (BoolNode, BoolNode, BoolNode) = (Variable('a'), Variable('b'), Variable('c'))

  def testParse(expected: BoolNode, representations: String*): Unit = for (toParse <- representations) {
    BoolNodeParser.parseBoolFormula(toParse) match {
      case Success(parsed) => assert(parsed == expected, s"""expected that parsing "$toParse" is equal to $expected""")
      case Failure(error)  => fail(s"""expected that parsing of "$toParse" succeeds, but got error $error!""")
    }
  }

  @Test
  def testNoOperators(): Unit = {
    testParse(aVar, "a", "A")
  }

  @Test
  def testConstants(): Unit = {
    testParse(TRUE, "true", "1", "TRUE")

    testParse(FALSE, "false", "0", "FALSE")
  }

  @Test
  def testSingleOperators(): Unit = {
    testParse(aVar and bVar, "a and b", "(a and b)")

    testParse(aVar or bVar, "a or b", "(a or b)")

    testParse(aVar nand bVar, "a nand b", "(a nand b)")

    testParse(aVar nor bVar, "a nor b", "(a nor b)")

    testParse(aVar xor bVar, "a xor b", "(a xor b)")

    testParse(aVar equiv bVar, "a equiv b", "(a equiv b)")

    testParse(aVar impl bVar, "a impl b", "(a impl b)")

    BoolNodeParser.parseBoolFormula("a test b") match {
      case Success(formula) => fail("Parsing of 'a test b' should fail, but succeeded with " + formula.toString)
      case _                => ()
    }

  }

  @Test
  def testDoubleOperators(): Unit = {
    testParse(aVar and bVar or cVar, "a and b or c")
    testParse(aVar and (bVar or cVar), "a and (b or c)")

    testParse(aVar and bVar or cVar, "(a and b) or c")

    testParse(aVar or (bVar and cVar), "a or b and c")
    testParse(aVar or (bVar and cVar), "a or (b and c)")

    testParse((aVar or bVar) and cVar, "(a or b) and c")
  }

  @Test
  def testTripleOperators(): Unit = {
    testParse((aVar and bVar) or (bVar and aVar), "a and b or b and a")
    testParse(aVar and (bVar or (bVar and aVar)), "a and (b or b and a)")
  }

}
