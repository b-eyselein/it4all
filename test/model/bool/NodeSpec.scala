package model.bool

import model.bool.NodeSpec._
import org.scalatest._

import scala.language.implicitConversions

object NodeSpec {

  implicit def char2Variable(char: Char): Variable = Variable(char)

  implicit def char2Scalanode(char: Char): ScalaNode = Variable(char)

  val (aVar, bVar, cVar): (Variable, Variable, Variable) = ('a', 'b', 'c')

}

class NodeSpec(protected val nodeUnderTest: ScalaNode) extends FlatSpec with Matchers {

  val (ff, ft, tf, tt) = (
    BoolTableRow(aVar -> false, bVar -> false),
    BoolTableRow(aVar -> false, bVar -> true),
    BoolTableRow(aVar -> true, bVar -> false),
    BoolTableRow(aVar -> true, bVar -> true)
  )

  def testEvaluate(expected: Array[Boolean]): Assertion = {
    nodeUnderTest(ff) shouldBe expected(0)
    nodeUnderTest(ft) shouldBe expected(1)
    nodeUnderTest(tf) shouldBe expected(2)
    nodeUnderTest(tt) shouldBe expected(3)
  }

  def testNegate(expected: ScalaNode): Assertion = nodeUnderTest.negate shouldBe expected

  def testContainedVariables(expected: Set[Variable]): Assertion = nodeUnderTest.usedVariables shouldBe expected

  def testGetAsString(expectedFalse: String, expectedTrue: String): Assertion = {
    nodeUnderTest.getAsString(false) shouldBe expectedFalse
    nodeUnderTest.getAsString(true) shouldBe expectedTrue
  }

  def evaluate(nodeUnderTest: ScalaNode, assignment: BoolTableRow, expected: Boolean): Assertion = nodeUnderTest(assignment) shouldBe expected

}

class NotSpec extends NodeSpec(NotScalaNode(aVar)) {
  "A NOT node \"not a\"" should "only contain the variable 'a'" in testContainedVariables(Set('a'))

  it should "evaluate to the opposite of 'a'" in testEvaluate(Array(true, true, false, false))

  it should "return a string representation of not a" in testGetAsString("not a", "(not a)")

  it should "negate to 'a'" in testNegate(aVar)
}

class VariableSpec extends NodeSpec(aVar) {
  "A Variable node of \"a\"" should "only contain the variable 'a'" in testContainedVariables(Set('a'))

  it should "have a variable of 'a'" in (nodeUnderTest match {
    case v: Variable => v.variable shouldBe 'a'
    case _           => fail()
  })

  it should "evaluate to certain values" in testEvaluate(Array(false, false, true, true))

  it should "always return a constant string representation" in testGetAsString("a", "a")

  it should "negate to \"not a\"" in testNegate(NotScalaNode(aVar))
}

class TrueSpec extends NodeSpec(TRUE) {
  "A TRUE constant" should "not contain any variables" in testContainedVariables(Set.empty)

  it should "always evalute to true" in testEvaluate(Array(true, true, true, true))

  it should "always return a string representation of" in testGetAsString("1", "1")

  it should "negate to the FALSE constant" in testNegate(FALSE)
}

class FalseSpec extends NodeSpec(FALSE) {
  "A FALSE constant" should "not contain any variables" in testContainedVariables(Set.empty)

  it should "always evalute to false" in testEvaluate(Array(false, false, false, false))

  it should "always return a string representation of \"0\"" in testGetAsString("0", "0")

  it should "negate to the TRUE constant" in testNegate(TRUE)
}

class AndSpec extends NodeSpec(AndScalaNode(aVar, bVar)) {
  "A ScalaNode of \"a and b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(false, false, false, true))

  it should "return a certain string representation" in testGetAsString("a and b", "(a and b)")

  it should "negate to \"a nand b\"" in testNegate(NAndScalaNode(aVar, bVar))
}

class OrSpec extends NodeSpec(OrScalaNode(aVar, bVar)) {
  "A ScalaNode of \"a or b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(false, true, true, true))

  it should "return a certain string representation" in testGetAsString("a or b", "(a or b)")

  it should "negate to \"a nand b\"" in testNegate(NOrScalaNode(aVar, bVar))
}

class NAndSpec extends NodeSpec(NAndScalaNode(aVar, bVar)) {
  "A ScalaNode of \"a nand b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(true, true, true, false))

  it should "return a certain string representation" in testGetAsString("a nand b", "(a nand b)")

  it should "negate to \"a nand b\"" in testNegate(AndScalaNode(aVar, bVar))
}

class NOrSpec extends NodeSpec(NOrScalaNode(aVar, bVar)) {
  "A ScalaNode of \"a nor b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(true, false, false, false))

  it should "return a certain string representation" in testGetAsString("a nor b", "(a nor b)")

  it should "negate to \"a nand b\"" in testNegate(OrScalaNode(aVar, bVar))
}

class XOrSpec extends NodeSpec(XOrScalaNode(aVar, bVar)) {
  "A ScalaNode of \"a xor b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(false, true, true, false))

  it should "return a certain string representation" in testGetAsString("a xor b", "(a xor b)")

  it should "negate to \"a nand b\"" in testNegate(Equivalency(aVar, bVar))
}

class EquivalencySpec extends NodeSpec(Equivalency(aVar, bVar)) {
  "A ScalaNode of \"a equiv b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(true, false, false, true))

  it should "return a certain string representation" in testGetAsString("a equiv b", "(a equiv b)")

  it should "negate to \"a nand b\"" in testNegate(XOrScalaNode(aVar, bVar))
}

class ImplicationSpec extends NodeSpec(Implication(aVar, bVar)) {
  "A ScalaNode of \"a impl b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(aVar, bVar))

  it should "evalute to certain values" in testEvaluate(Array(true, true, false, true))

  it should "return a certain string representation" in testGetAsString("a impl b", "(a impl b)")

  it should "negate to \"a nand b\"" in testNegate(AndScalaNode(aVar, ScalaNode.not(bVar)))
}

