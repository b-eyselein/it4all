package model.bool

import model.bool.NodeSpec._
import model.essentials._
import org.scalatest._

object NodeSpec {

  implicit def char2Variable(char: Char): Variable = Variable(char)

  implicit def char2Scalanode(char: Char): ScalaNode = Variable(char)

}

class NodeSpec(protected val nodeUnderTest: ScalaNode) extends FlatSpec {

  val (a, b, c): (Variable, Variable, Variable) = ('a', 'b', 'c')

  val (ff, ft, tf, tt) = (
                           BoolAssignment(a -> false, b -> false),
                           BoolAssignment(a -> false, b -> true),
                           BoolAssignment(a -> true, b -> false),
                           BoolAssignment(a -> true, b -> true))

  def testEvaluate(expected: Array[Boolean]) {
    assert(nodeUnderTest.evaluate(ff) == expected(0))
    assert(nodeUnderTest.evaluate(ft) == expected(1))
    assert(nodeUnderTest.evaluate(tf) == expected(2))
    assert(nodeUnderTest.evaluate(tt) == expected(3))
  }

  def testNegate(expected: ScalaNode): Assertion = assert(nodeUnderTest.negate == expected)

  def testContainedVariables(expected: Set[Variable]): Assertion = assert(nodeUnderTest.usedVariables == expected)

  def testGetAsString(expectedFalse: String, expectedTrue: String) {
    assert(nodeUnderTest.getAsString(false) == expectedFalse)
    assert(nodeUnderTest.getAsString(true) == expectedTrue)
  }

  def evaluate(nodeUnderTest: ScalaNode, assignment: BoolAssignment, expected: Boolean): Assertion = assert(nodeUnderTest.evaluate(assignment) == expected)

}

class NotSpec extends NodeSpec(new NotScalaNode('a')) {
  "A NOT node \"not a\"" should "only contain the variable 'a'" in testContainedVariables(Set('a'))

  it should "evaluate to the opposite of 'a'" in testEvaluate(Array(true, true, false, false))

  it should "return a string representation of not a" in testGetAsString("not a", "(not a)")

  it should "negate to 'a'" in testNegate(a)
}

class VariableSpec extends NodeSpec(Variable('a')) {
  "A Variable node of \"a\"" should "only contain the variable 'a'" in testContainedVariables(Set('a'))

  it should "have a variable of 'a'" in assert(nodeUnderTest.asInstanceOf[Variable].variable == 'a')

  it should "evaluate to certain values" in testEvaluate(Array(false, false, true, true))

  it should "always return a constant string representation" in testGetAsString("a", "a")

  it should "negate to \"not a\"" in testNegate(NotScalaNode(a))
}

class TrueSpec extends NodeSpec(TRUE) {
  "A TRUE constant \"true\"" should "not contain any variables" in testContainedVariables(Set.empty)

  it should "always evalute to true" in testEvaluate(Array(true, true, true, true))

  it should "always return a string representation of" in testGetAsString("1", "1")

  it should "negate to the FALSE constant" in testNegate(FALSE)
}

class FalseSpec extends NodeSpec(FALSE) {
  "A FALSE constant \"false\"" should "not contain any variables" in testContainedVariables(Set.empty)

  it should "always evalute to false" in testEvaluate(Array(false, false, false, false))

  it should "always return a string representation of \"0\"" in testGetAsString("0", "0")

  it should "negate to the TRUE constant" in testNegate(TRUE)
}

class AndSpec extends NodeSpec(new AndScalaNode('a', 'b')) {
  "A ScalaNode of \"a and b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(false, false, false, true))

  it should "return a certain string representation" in testGetAsString("a and b", "(a and b)")

  it should "negate to \"a nand b\"" in testNegate(NAndScalaNode('a', 'b'))
}

class OrSpec extends NodeSpec(new OrScalaNode('a', 'b')) {
  "A ScalaNode of \"a or b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(false, true, true, true))

  it should "return a certain string representation" in testGetAsString("a or b", "(a or b)")

  it should "negate to \"a nand b\"" in testNegate(NOrScalaNode('a', 'b'))
}

class NAndSpec extends NodeSpec(new NAndScalaNode('a', 'b')) {
  "A ScalaNode of \"a nand b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(true, true, true, false))

  it should "return a certain string representation" in testGetAsString("a nand b", "(a nand b)")

  it should "negate to \"a nand b\"" in testNegate(AndScalaNode('a', 'b'))
}

class NOrSpec extends NodeSpec(new NOrScalaNode('a', 'b')) {
  "A ScalaNode of \"a nor b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(true, false, false, false))

  it should "return a certain string representation" in testGetAsString("a nor b", "(a nor b)")

  it should "negate to \"a nand b\"" in testNegate(new OrScalaNode('a', 'b'))
}

class XOrSpec extends NodeSpec(new XOrScalaNode('a', 'b')) {
  "A ScalaNode of \"a xor b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(false, true, true, false))

  it should "return a certain string representation" in testGetAsString("a xor b", "(a xor b)")

  it should "negate to \"a nand b\"" in testNegate(new Equivalency('a', 'b'))
}

class EquivalencySpec extends NodeSpec(new Equivalency('a', 'b')) {
  "A ScalaNode of \"a equiv b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(true, false, false, true))

  it should "return a certain string representation" in testGetAsString("a equiv b", "(a equiv b)")

  it should "negate to \"a nand b\"" in testNegate(XOrScalaNode('a', 'b'))
}

class ImplicationSpec extends NodeSpec(new Implication('a', 'b')) {
  "A ScalaNode of \"a impl b\"" should "contain exactly the variables 'a' and 'b'" in testContainedVariables(Set(a, b))

  it should "evalute to certain values" in testEvaluate(Array(true, true, false, true))

  it should "return a certain string representation" in testGetAsString("a impl b", "(a impl b)")

  it should "negate to \"a nand b\"" in testNegate(AndScalaNode('a', ScalaNode.not('b')))
}

