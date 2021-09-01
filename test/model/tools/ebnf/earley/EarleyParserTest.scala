package model.tools.ebnf.earley

import model.tools.ebnf.DefaultGrammars
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EarleyParserTest extends AnyFlatSpec with Matchers {

  import model.tools.ebnf.DefaultGrammars._

  val debug = false //true

  behavior of "EarleyParser"

  private val sentences = Seq(
    "book the flight through houston",
    "john saw the boy with the telescope",
    "john thinks that bill ate a banana"
  ).map { _.split(" ").toSeq }

  it should "parse an english sentence" in {
    val tableState1: Option[TableState] = new EarleyParser(englishGrammar).parse(Seq("john", "ate", "a", "banana"))

    tableState1.isDefined shouldBe true

    val trees = tableState1.get.trees(englishGrammar)

    trees.size shouldBe 1

    if (debug) {
      trees.foreach { s => println(s.stringify()) }
    }
  }

  it should "parse more english sentences" in {
    val parser = new EarleyParser(DefaultGrammars.englishGrammar)

    sentences.foreach { it =>
      val t = parser.parse(it)

      t.isDefined shouldBe true
    }
  }

  it should "parse ambiguous grammars" in {
    val state = new EarleyParser(ambiguousGrammar).parse(Seq("fish", "people", "fish", "tanks"), debug)

    state.isDefined shouldBe true

    val trees = state.get.trees(ambiguousGrammar)

    if (debug) {
      trees.foreach { s => println(s.stringify()) }
    }

    trees.size shouldBe 3
  }

  it should "test binary palindromes" in {
    val parser = new EarleyParser(binaryPalindromesBaseNormalized)

    val t0 = parser.parse(Seq("0"))
    t0.isDefined shouldBe true

    val t1 = parser.parse(Seq("1"))
    t1.isDefined shouldBe true

    val t00 = parser.parse(Seq("0", "0"))
    t00.isDefined shouldBe true

    val t11 = parser.parse(Seq("1", "1"))
    t11.isDefined shouldBe true

    val t10 = parser.parse(Seq("1", "0"))
    t10 shouldBe None

    val t01 = parser.parse(Seq("0", "1"))
    t01 shouldBe None
  }

  it should "handle empty words" in {
    val parser = new EarleyParser(binaryPalindromesBaseNormalizedWithEmpty)

    val t0 = parser.parse(Seq("0"))
    t0.isDefined shouldBe true

    val t1 = parser.parse(Seq("1"))
    t1.isDefined shouldBe true

    val t00 = parser.parse(Seq("0", "0"))
    t00.isDefined shouldBe true

    val t11 = parser.parse(Seq("1", "1"))
    t11.isDefined shouldBe true

    val t10 = parser.parse(Seq("1", "0"))
    t10 shouldBe None

    val t01 = parser.parse(Seq("0", "1"))
    t01 shouldBe None

    val tn = parser.parse(Seq("1", "0", "0", "1", "0", "0", "1"))
    tn.isDefined shouldBe true
  }

  it should "handle an ebnf grammar" in {
    val parser = new EarleyParser(numbersGrammar)

    val t0 = parser.parse(Seq("0"))
    t0.isDefined shouldBe true

    val t1 = parser.parse(Seq("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"))
    t1.isDefined shouldBe true

    parser.parse(Seq("1", "2", "a", "3", "4", "5", "6", "7", "8", "9", "0")) shouldBe None
  }

}
